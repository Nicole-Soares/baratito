package com.baratito.server.scraper.impl;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.scraper.Scraper;
import com.baratito.server.scraper.core.VtexCore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MasOnlineScraper implements Scraper {

    private static final String BASE_URL = "https://www.masonline.com.ar";
    private static final String SOURCE   = "masonline";

    private final VtexCore vtexCore;

    public MasOnlineScraper(VtexCore vtexCore) {
        this.vtexCore = vtexCore;
    }

    @Override
    public String getNombre() {
        return SOURCE;
    }

    @Override
    public List<ProductoSchema> buscar(String query) {
        return vtexCore.buscar(new VtexCore.VtexCoreProps(query, BASE_URL, SOURCE));
    }
}