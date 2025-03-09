using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;

namespace ServerSite.Controllers;

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
}