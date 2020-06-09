package net.rusoil.steam.parser.service;

import net.rusoil.steam.parser.exception.ForbiddenException;
import net.rusoil.steam.parser.exception.ResourceNotFoundException;
import net.rusoil.steam.parser.model.Price;
import net.rusoil.steam.parser.model.SteamApp;
import net.rusoil.steam.parser.repository.SteamAppRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParserService {

    private final SteamAppRepository steamAppRepository;

    @Autowired
    public ParserService(SteamAppRepository steamAppRepository) {
        this.steamAppRepository = steamAppRepository;
    }

    public void parse(Long appId) {
        Document page = loadPage(appId);
        SteamApp steamApp = parsePage(page);
        steamApp.setAppId(appId);
        steamApp.setParseDateTime(LocalDateTime.now());
        steamAppRepository.save(steamApp);
    }

    private Document loadPage(Long appId) {
        Document page;
        String url = "https://store.steampowered.com/app/" + appId + "/?l=russian";
        try {
            page = Jsoup.connect(url)
                    .cookies(createCookies())
                    .get();
        } catch (IOException ex) {
            throw new ForbiddenException("Can't connect to " + url);
        }

        if (page != null && page.location().equals("https://store.steampowered.com/")) {
            throw new ResourceNotFoundException("Wrong appId!");
        }

        return page;
    }

    private Map<String, String> createCookies() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("wants_mature_content", "1");
        cookies.put("birthtime", "249775201");
        cookies.put("lastagecheckage", "1-0-1978");
        cookies.put("steamCountry", "RU");

        return cookies;
    }

    private SteamApp parsePage(Document page) {
        SteamApp app = new SteamApp();
        app.setName(parseAppName(page));
        app.setAllReviews(parseAllReviews(page));
        List<Price> prices = parsePrices(page);
        prices.forEach(app::addPrice);

        return app;
    }

    private String parseAppName(Document page) {
        return page.getElementsByClass("apphub_AppName").get(0).text();
    }

    private String parseAllReviews(Document page) {
        String allReviews = page.getElementsByClass("user_reviews_summary_row")
                .stream()
                .filter(div -> div.text().contains("Все обзоры"))
                .map(Element::text)
                .findFirst().orElse(null);

        return allReviews == null ? "" : allReviews.substring(12);
    }

    private List<Price> parsePrices(Document page) {
        Elements rawPrices = page.getElementsByClass("game_area_purchase_game");
        List<Price> prices = new ArrayList<>();
        for (Element element : rawPrices) {
            prices.add(createPrice(element));
        }

        return prices;
    }

    private Price createPrice(Element element) {
        var price = new Price();
        String priceName = parsePriceName(element);
        String priceStr = parsePrice(element);
        if (priceStr.contains("Бесплатно")) {
            priceName = priceName.substring(3);
        }
        price.setName(priceName);
        price.setPrice(priceStr);

        return price;
    }

    private String parsePriceName(Element element) {
        String name = element.childNodes()
                .stream()
                .filter(el -> el.toString().contains("<h1>"))
                .map(el -> el.childNode(0).toString().stripLeading().stripTrailing())
                .findFirst().orElse(null);

        return name == null ? null : name.substring(7);
    }

    private String parsePrice(Element element) {
        String price = null;
        if (element.getElementsByClass("game_purchase_price").size() > 0) {
            price = element.getElementsByClass("game_purchase_price").text();
        } else if (element.getElementsByClass("discount_final_price").size() > 0) {
            price = element.getElementsByClass("discount_final_price").text();
        }

        return price;
    }


}
