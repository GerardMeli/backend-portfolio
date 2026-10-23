package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CertificationMapper {

    public CertificationDto toDto(Certification c) {
        return new CertificationDto(
                c.getId(),
                c.getName(),
                c.getIssuer(),
                c.getIssuedOn(),
                c.getExpiresOn(),
                c.getCredentialUrl(),
                c.getMarkets(),
                c.getSortOrder(),
                c.isVisible()
        );
    }

    public Certification toEntity(CertificationDto d) {
        Certification c = new Certification();
        update(d, c);
        return c;
    }

    public void update(CertificationDto d, Certification c) {
        c.setName(MapperUtils.copy(d.name()));
        c.setIssuer(d.issuer());
        c.setIssuedOn(d.issuedOn());
        c.setExpiresOn(d.expiresOn());
        c.setCredentialUrl(d.credentialUrl());
        c.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) c.setSortOrder(d.sortOrder());
        c.setVisible(MapperUtils.orElse(d.visible(), c.isVisible()));
    }

    public CertificationView toView(Certification c, ViewContext ctx) {
        return new CertificationView(
                ctx.text(c.getName()),
                c.getIssuer(), c.getIssuedOn(),
                c.getExpiresOn(),
                c.getCredentialUrl()
        );
    }
}
