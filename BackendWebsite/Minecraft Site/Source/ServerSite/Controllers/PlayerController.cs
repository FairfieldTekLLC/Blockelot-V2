using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using ServerSite.DataContracts.Rpg;
using ServerSite.WorldEdit.Data;

namespace ServerSite.Controllers
{
    [Consumes("application/json")]
    [Produces("application/json")]
    [Route("api/Player")]
    public class PlayerController : Controller
    {
        private readonly IWebHostEnvironment _hostingEnvironment;

        public PlayerController(IWebHostEnvironment hostingEnvironment)
        {
            _hostingEnvironment = hostingEnvironment;
        }

        //[Route("v1/MobExp")]
        //[HttpPost]
        //public IActionResult MobExp()
        //{
        //    return Json(Rpg.MobExp());
        //}

        //[Route("v1/PlayerLoad")]
        //[HttpPost]
        //public IActionResult PlayerLoad([FromBody] PlayerInfoRequest playerInfo)
        //{
        //    return Json(Rpg.GetPlayer(playerInfo.Uuid));
        //}

        //[Route("v1/PlayerSave")]
        //[HttpPost]
        //public IActionResult PlayerSave([FromBody] PlayerInfoResponse playerInfo)
        //{
        //    return Json(Rpg.PlayerSave(playerInfo));
        //}
    }
}