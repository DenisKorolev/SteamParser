package net.rusoil.steam.parser.controller;

import net.rusoil.steam.parser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/parser/")
public class ParserController {

    private final ParserService service;

    @Autowired
    public ParserController(ParserService service) {
        this.service = service;
    }

    @PostMapping("/{appId}")
    public void parse(@PathVariable Long appId) {
        service.parse(appId);
    }
}
