package net.rusoil.steam.parser.service;

import net.rusoil.steam.parser.exception.ResourceNotFoundException;
import net.rusoil.steam.parser.model.SteamApp;
import net.rusoil.steam.parser.repository.SteamAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamAppService {

    private final SteamAppRepository steamAppRepository;

    @Autowired
    public SteamAppService(SteamAppRepository steamAppRepository) {
        this.steamAppRepository = steamAppRepository;
    }

    public SteamApp getLast(Long appId) {
        List<SteamApp> steamApps = steamAppRepository.getByAppIdOrderByParseDateTimeDesc(appId);
        if (steamApps.size() > 0) {
            return steamApps.get(0);
        } else {
            throw new ResourceNotFoundException("No data for appId " + appId);
        }
    }

    public List<SteamApp> getHistory(Long appId) {
        return steamAppRepository.getByAppIdOrderByParseDateTimeDesc(appId);
    }
}
