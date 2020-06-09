package net.rusoil.steam.parser.controller;

import net.rusoil.steam.parser.model.SteamApp;
import net.rusoil.steam.parser.service.SteamAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/steamApp/")
public class SteamAppController {

    private final SteamAppService steamAppService;

    @Autowired
    public SteamAppController(SteamAppService steamAppService) {
        this.steamAppService = steamAppService;
    }

    @GetMapping("/last/{appId}")
    public SteamApp getLast(@PathVariable Long appId) {
        return steamAppService.getLast(appId);
    }

    @GetMapping("/history/{appId}")
    public List<SteamApp> getHistory(@PathVariable Long appId) {
        return steamAppService.getHistory(appId);
    }
}
