//  Fairfield Tek L.L.C.
//  Copyright (c) 2016, Fairfield Tek L.L.C.
//  
//  
// THIS SOFTWARE IS PROVIDED BY WINTERLEAF ENTERTAINMENT LLC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL WINTERLEAF ENTERTAINMENT LLC BE LIABLE FOR ANY DIRECT, INDIRECT, 
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
// DAMAGE. 
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Org.BouncyCastle.Utilities.Encoders;
using ServerSite.DataContracts.BlockBank;
using ServerSite.DataContracts.WorldEdit;
using ServerSite.WorldEdit.Data;
using ServerSite.WorldEdit.Utils;

namespace ServerSite.Controllers
{
    [Consumes("application/json")]
    [Produces("application/json")]
    [Route("api/WorldEditor")]
    public class WorldEditorController : Controller
    {
        private readonly IWebHostEnvironment _hostingEnvironment;

        private readonly JsonSerializerOptions Options = new JsonSerializerOptions
        { PropertyNameCaseInsensitive = false };

        private readonly PaletteEntry[] PEEmpty = { new PaletteEntry { Id = 0, Value = "" } };

        public WorldEditorController(IWebHostEnvironment hostingEnvironment)
        {
            _hostingEnvironment = hostingEnvironment;
        }

        [Route("v1/Authenticate")]
        [HttpPost]
        public async Task<IActionResult> Authenticate([FromBody] AuthenticateRequest authRequest)
        {
            string authcode = await Users.AuthUser(authRequest.Uuid);

            return Json(string.IsNullOrEmpty(authcode)
                ? new AuthenicateResponse
                {
                    Message =
                        "Minecraft Account not linked to email address.  Type /b.reg [email address] to register your player.",
                    WasSuccessful = false,
                    Uuid = authRequest.Uuid,
                    Auth = ""
                }
                : new AuthenicateResponse
                {
                    Message = "Player linked to Blockelot!", //"Auth Code sent to email, use /fft.Auth <Auth Code>",
                    WasSuccessful = true,
                    Uuid = authRequest.Uuid,
                    Auth = authcode
                }, Options);
        }

        [Route("v1/DirCd")]
        [HttpPost]
        public IActionResult DirCd([FromBody] CdRequest cdRequest)
        {
            string lastAuth = Users.Login(cdRequest.Uuid, cdRequest.Auth);
            int userId = Users.GetUserId(cdRequest.Uuid);
            if (string.IsNullOrEmpty(lastAuth))
                return Json(new LsResponse
                {
                    Uuid = cdRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = false,
                    Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                    Auth = "",
                    DirectoryPath = ""
                }, Options);

            string nDir = cdRequest.CurrentDirectory;

            if (!nDir.Trim().EndsWith("/"))
                nDir = cdRequest.CurrentDirectory.Trim() + "/";

            //../test/

            if (cdRequest.TargetDirectory.Equals(".."))
            {
                if (nDir.Equals("../"))
                    return Json(new LsResponse
                    {
                        Uuid = cdRequest.Uuid,
                        WasSuccessful = false,
                        IsAuthorized = true,
                        Message = "Already at root.",
                        Auth = lastAuth,
                        DirectoryPath = cdRequest.CurrentDirectory
                    }, Options);

                string t = nDir.Substring(0, nDir.Length - 1); //Remove the last slash..
                nDir = nDir.Substring(0, t.LastIndexOf("/", StringComparison.InvariantCultureIgnoreCase));
                if (!nDir.EndsWith("/"))
                    nDir = nDir + "/";
            }
            else
            {
                nDir = nDir + cdRequest.TargetDirectory.Trim() + "/";
            }

            int x = Users.FindFolderFromPath(userId, nDir);

            if (x == -1)
                return Json(new LsResponse
                {
                    Uuid = cdRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = true,
                    Message = "Invalid Directory Name.",
                    Auth = lastAuth,
                    DirectoryPath = cdRequest.CurrentDirectory
                }, Options);
            return Json(new LsResponse
            {
                Uuid = cdRequest.Uuid,
                WasSuccessful = true,
                IsAuthorized = true,
                Message = "Changed directory.",
                Auth = lastAuth,
                DirectoryPath = nDir
            }, Options);
        }

        [Route("v1/DirLs")]
        [HttpPost]
        public IActionResult DirLs([FromBody] LsRequest dirLsRequest)
        {
            string lastAuth = Users.Login(dirLsRequest.Uuid, dirLsRequest.Auth);
            int userId = Users.GetUserId(dirLsRequest.Uuid);

            if (string.IsNullOrEmpty(lastAuth))
                return Json(new LsResponse
                {
                    Uuid = dirLsRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = false,
                    Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                    Auth = "",
                    DirectoryPath = ""
                }, Options);

            if (!dirLsRequest.CurrentDirectory.Trim().EndsWith("/"))
                dirLsRequest.CurrentDirectory = dirLsRequest.CurrentDirectory.Trim() + "/";

            List<DirectoryElement> c = Users.GetFolderContents(userId, dirLsRequest.CurrentDirectory);



            JsonResult what =  Json(new LsResponse
            {
                Contents =c ,
                DirectoryPath = dirLsRequest.CurrentDirectory,
                Uuid = dirLsRequest.Uuid,
                IsAuthorized = true,
                Message = "Listing Ok!",
                WasSuccessful = true,
                Auth = lastAuth
            }, Options);

            return what;
        }

        [Route("v1/DirMk")]
        [HttpPost]
        public IActionResult DirMk([FromBody] MkRequest mkRequest)
        {
            try
            {


                string lastAuth = Users.Login(mkRequest.Uuid, mkRequest.Auth);
                int userId = Users.GetUserId(mkRequest.Uuid);
                if (string.IsNullOrEmpty(lastAuth))
                    return Json(new LsResponse
                    {
                        Uuid = mkRequest.Uuid,
                        WasSuccessful = false,
                        IsAuthorized = false,
                        Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                        Auth = "",
                        DirectoryPath = ""
                    }, Options);

                Tuple<bool, string> r = Users.CreateFolder(userId, mkRequest.CurrentDirectory, mkRequest.TargetDirectory);

                if (r.Item1)
                    return Json(new MkResponse
                    {
                        Uuid = mkRequest.Uuid,
                        WasSuccessful = true,
                        IsAuthorized = true,
                        Message = "Directory Removed.",
                        Auth = lastAuth,
                        DirectoryPath = mkRequest.CurrentDirectory
                    }, Options);

                return Json(new MkResponse
                {
                    Uuid = mkRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = true,
                    Message = r.Item2,
                    Auth = lastAuth,
                    DirectoryPath = mkRequest.CurrentDirectory
                }, Options);
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                return Json(new MkResponse
                {
                    Uuid = mkRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = true,
                    Message = e.Message + " " + e.StackTrace,
                    Auth = "",
                    DirectoryPath = mkRequest.CurrentDirectory
                }, Options);
            }


        }

        [Route("v1/DirRm")]
        [HttpPost]
        public IActionResult DirRm([FromBody] RmRequest rmRequest)
        {
            string lastAuth = Users.Login(rmRequest.Uuid, rmRequest.Auth);
            int userId = Users.GetUserId(rmRequest.Uuid);
            if (string.IsNullOrEmpty(lastAuth))
                return Json(new LsResponse
                {
                    Uuid = rmRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = false,
                    Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                    Auth = "",
                    DirectoryPath = ""
                }, Options);

            Tuple<bool, string> r = Users.Delete(userId, rmRequest.CurrentDirectory, rmRequest.TargetDirectory);

            if (r.Item1)
                return Json(new RmResponse
                {
                    Uuid = rmRequest.Uuid,
                    WasSuccessful = true,
                    IsAuthorized = true,
                    Message = r.Item2,
                    Auth = lastAuth,
                    DirectoryPath = rmRequest.CurrentDirectory
                }, Options);

            return Json(new RmResponse
            {
                Uuid = rmRequest.Uuid,
                WasSuccessful = false,
                IsAuthorized = true,
                Message = r.Item2,
                Auth = lastAuth,
                DirectoryPath = rmRequest.CurrentDirectory
            }, Options);
        }

        [Route("v1/Index")]
        [HttpGet]
        public ActionResult Index()
        {
            string webRootPath = _hostingEnvironment.WebRootPath;
            string contentRootPath = _hostingEnvironment.ContentRootPath;
            return Content(webRootPath + "\n" + contentRootPath);
        }


        [Route("v1/Version")]
        [HttpGet]
        public ActionResult Version(string version, string serverName, string worldId)
        {
            if (worldId == "NEWSERVER")
                worldId = "";
            try
            {
                string r = Users.RegisterServer(Request.HttpContext.Connection.RemoteIpAddress.ToString(), version, serverName, worldId) + "|" + Program.Version;
                return Content(r);
            }
            catch (Exception e)
            {
                return Content(e.Message + e.StackTrace);
            }
        }

        [Route("v1/Load")]
        [HttpPost]
        public IActionResult Load([FromBody] SchematicDataDownloadRequest request)
        {
            string lastAuth = Users.Login(request.Uuid, request.Auth);

            int userId = Users.GetUserId(request.Uuid);

            if (string.IsNullOrEmpty(lastAuth))
                return Json(new SchematicDataDownloadResponse
                {
                    Uuid = request.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = false,
                    Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                    Auth = "",
                    FileName = request.FileName,
                    Blocks = new int[0],
                    BlockDataPalette = PEEmpty,
                    BlockTypePalette = PEEmpty,
                    BlockInvePalette = PEEmpty,
                    TotalNumberOfBlocks = 0
                }, Options);



            (bool success, string msg, string blocks, List<PaletteEntry> blockDataPalette, List<PaletteEntry> blockTypePalette, List<PaletteEntry> blockInvePalette, int blockCount) = Users.LoadSchematic(userId, request.CurrentDirectory, request.FileName);




            string[] parts = blocks.Split("|");
            List<int> pints = new List<int>();
            foreach (string part in parts)
            {
                if (int.TryParse(part, out int i))
                {
                    pints.Add(i);
                }
            }

            if (success)
                return Json(new SchematicDataDownloadResponse
                {
                    Uuid = request.Uuid,
                    WasSuccessful = true,
                    IsAuthorized = true,
                    Message = "Schematic '" + request.FileName + "' loaded.",
                    Auth = lastAuth,
                    FileName = request.FileName,
                    Blocks = pints.ToArray(),
                    BlockDataPalette = blockDataPalette.ToArray(),
                    BlockTypePalette = blockTypePalette.ToArray(),
                    BlockInvePalette = blockInvePalette.ToArray(),
                    TotalNumberOfBlocks = blockCount
                }, Options);
            return Json(new SchematicDataDownloadResponse
            {
                Uuid = request.Uuid,
                WasSuccessful = false,
                IsAuthorized = true,
                Message = msg,
                Auth = lastAuth,
                FileName = request.FileName,
                Blocks = new int[0],
                BlockDataPalette = PEEmpty,
                BlockTypePalette = PEEmpty,
                BlockInvePalette = PEEmpty,
                TotalNumberOfBlocks = 0
            }, Options);
        }

        [Route("v1/Login")]
        [HttpPost]
        public IActionResult Login([FromBody] LoginRequest loginRequest)
        {
            string lastAuth = Users.Login(loginRequest.Uuid, loginRequest.Auth, loginRequest.Wid);
            if (string.IsNullOrEmpty(lastAuth))
                return Json(new LoginResponse
                {
                    Uuid = loginRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = false,
                    Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                    CurrentPath = ""
                }, Options);

            JsonSerializerOptions t = new JsonSerializerOptions();


            return Json(new LoginResponse
            {
                IsAuthorized = true,
                Auth = lastAuth,
                Uuid = loginRequest.Uuid,
                WasSuccessful = true,
                Message = "Welcome to the Fairfield Tek L.L.C. Minecraft Library.",
                CurrentPath = "../"
            }, Options);
        }

        // http://url/api/worldeditor/v1/sayhi
        [Route("v1/Register")]
        [HttpPost]
        public async Task<IActionResult> Register([FromBody] RegisterRequest registerRequest)
        {
            if (!registerRequest.EmailAddress.IsValidEmail())
                return Json(new RegisterResponse
                { Uuid = registerRequest.Uuid, Message = "Invalid Email Address", WasSuccessful = false });

            if (string.IsNullOrEmpty(registerRequest.Uuid))
                return Json(new RegisterResponse
                { Uuid = registerRequest.Uuid, Message = "Invalid UUID", WasSuccessful = false });

            string email = Users.GetEmail(registerRequest.Uuid);

            if (email != null)
                return Json(new RegisterResponse
                {
                    Uuid = registerRequest.Uuid,
                    Message = "Account already registered to '" + email + "'.",
                    WasSuccessful = false
                }, Options);

            string auth = Users.RegisterNewUser(registerRequest.Uuid, registerRequest.EmailAddress);


            return Json(new RegisterResponse
            {
                Uuid = registerRequest.Uuid,
                WasSuccessful = true,
                Auth = auth,
                Message =
                    "Player registered to email account." //"An email was sent to you with your Auth Code, use /FFT.Auth <AuthCode> to login."
            }, Options);
        }

        public string SanitizePath(string path)
        {
            if (!path.Trim().EndsWith("/"))
                return path.Trim() + "/";
            return path.Trim();
        }

        [Route("v1/Save")]
        [HttpPost]
        public IActionResult Save([FromBody] SchematicDataRequest schematicDataRequest)
        {
            string lastAuth = Users.Login(schematicDataRequest.Uuid, schematicDataRequest.Auth);

            int userId = Users.GetUserId(schematicDataRequest.Uuid);

            if (string.IsNullOrEmpty(lastAuth))
                return Json(new SchematicDataResponse
                {
                    Uuid = schematicDataRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = false,
                    Message = "Authorization code is wrong, use /fft.auth to receive new auth code.",
                    Auth = ""
                }, Options);

            if (schematicDataRequest.SchematicId == -1)
            {
                Tuple<bool, string, int> r = Users.SaveSchematic(userId, schematicDataRequest.CurrentDirectory,
                    schematicDataRequest.FileName, schematicDataRequest, _hostingEnvironment.ContentRootPath);
                if (r.Item1)
                    return Json(new SchematicDataResponse
                    {
                        Uuid = schematicDataRequest.Uuid,
                        WasSuccessful = true,
                        IsAuthorized = true,
                        Message = "",
                        Auth = lastAuth,
                        SchematicId = r.Item3
                    }, Options);

                return Json(new SchematicDataResponse
                {
                    Uuid = schematicDataRequest.Uuid,
                    WasSuccessful = false,
                    IsAuthorized = true,
                    Message = r.Item2,
                    Auth = lastAuth
                }, Options);
            }

            Base64Encoder c = new Base64Encoder();

            string data = schematicDataRequest.Blocks;

            if (data.Length <= 0)
                return Json(new SchematicDataResponse
                {
                    Uuid = schematicDataRequest.Uuid,
                    WasSuccessful = true,
                    IsAuthorized = true,
                    Message = "Finished",
                    Auth = lastAuth,
                    SchematicId = schematicDataRequest.SchematicId
                }, Options);


            void LoadEm(object callback)
            {
                Users.BulkLoad(schematicDataRequest.SchematicId, data, _hostingEnvironment.ContentRootPath);
            }

            ThreadPool.QueueUserWorkItem(new WaitCallback(LoadEm));


            return Json(new SchematicDataResponse
            {
                Uuid = schematicDataRequest.Uuid,
                WasSuccessful = true,
                IsAuthorized = true,
                Message = schematicDataRequest.Blocks.Length + " blocks added.",
                Auth = lastAuth,
                SchematicId = schematicDataRequest.SchematicId
            }, Options);

        }


        [Route("v1/BBDR")]
        [HttpPost]
        public IActionResult BBDR([FromBody] BlockBankDepositRequest request)
        {
            BlockBankDepositResponse response = new BlockBankDepositResponse
            {
                Auth = Users.Login(request.Uuid, request.Auth, request.Wid)
            };
            int userId = Users.GetUserId(request.Uuid);

            if (string.IsNullOrEmpty(response.Auth))
            {
                response.Uuid = request.Uuid;
                response.Auth = "";
                response.Success = false;
                return Json(response, Options);
            }

            foreach (BlockBankInventoryItem itm in request.ToDeposit)
            {
                if (itm.Count <= 0)
                {
                    response.Uuid = request.Uuid;
                    response.Auth = "";
                    response.Success = false;
                    return Json(response, Options);
                }

                Users.DepositMaterial(userId, request.Wid, itm.MaterialName, itm.Count);
            }


            response.Success = true;
            response.Uuid = request.Uuid;
            return Json(response, Options);
        }


        [Route("v1/BBIRQ")]
        [HttpPost]
        public IActionResult BBIRQ([FromBody] BlockBankInventoryRequest request)
        {
            BlockBankInventoryResponse response = new BlockBankInventoryResponse();
            response.Auth = Users.Login(request.Uuid, request.Auth, request.Wid);
            int userId = Users.GetUserId(request.Uuid);

            if (string.IsNullOrEmpty(response.Auth))
            {
                response.Uuid = request.Uuid;
                response.Auth = "";
                response.Items = new List<BlockBankInventoryItem>().ToArray();
                response.Success = false;
                return Json(response, Options);
            }

            response.Items = Users.GetBlockBankInventory(userId, request.Wid,request.SearchCriteria).ToArray();
            response.Uuid = request.Uuid;
            response.Success = true;
            return Json(response, Options);
        }

        [Route("v1/BBWR")]
        [HttpPost]
        public IActionResult BBWR([FromBody] BlockBankWithdrawlRequest request)
        {
            BlockBankWithdrawlResponse response = new BlockBankWithdrawlResponse();
            response.Auth = Users.Login(request.Uuid, request.Auth, request.Wid);
            int userId = Users.GetUserId(request.Uuid);

            if (string.IsNullOrEmpty(response.Auth))
            {
                response.Uuid = request.Uuid;
                response.Auth = "";
                response.Amount = 0;
                response.Material = "";
                response.Success = false;
                return Json(response, Options);
            }

            if (request.Amount <= 0)
            {
                response.Uuid = request.Uuid;
                response.Auth = "";
                response.Amount = 0;
                response.Material = "";
                response.Success = false;
                return Json(response, Options);
            }


            response.Amount = Users.WithDrawlMaterial(userId, request.Wid, request.Material, request.Amount);
            response.Material = request.Material;
            response.Uuid = request.Uuid;
            response.Success = true;
            return Json(response, Options);
        }
    }
}