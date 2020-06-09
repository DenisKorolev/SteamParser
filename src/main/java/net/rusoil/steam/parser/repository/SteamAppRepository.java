package net.rusoil.steam.parser.repository;

import net.rusoil.steam.parser.model.SteamApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SteamAppRepository extends JpaRepository<SteamApp, Long> {

    List<SteamApp> getByAppIdOrderByParseDateTimeDesc(Long appId);
}
