package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ProfileMapper {

    public ProfileDto toDto(Profile p) {
        return new ProfileDto(p.getId(), p.getFullName(), p.getHeadline(), p.getSummary(), p.getEmail(),
                p.getPhone(), p.getCity(), p.getCountry(), p.getPhotoUrl(), p.getBirthDate(),
                p.getNationality(), p.getMaritalStatus(), p.getWorkAuthorization(), p.getLinkedinUrl(),
                p.getGithubUrl(), p.getWebsiteUrl());
    }

    public Profile toEntity(ProfileDto d) {
        Profile p = new Profile();
        update(d, p);
        return p;
    }

    public void update(ProfileDto d, Profile p) {
        p.setFullName(d.fullName());
        p.setHeadline(MapperUtils.copy(d.headline()));
        p.setSummary(MapperUtils.copy(d.summary()));
        p.setEmail(d.email());
        p.setPhone(d.phone());
        p.setCity(d.city());
        p.setCountry(d.country());
        p.setPhotoUrl(d.photoUrl());
        p.setBirthDate(d.birthDate());
        p.setNationality(d.nationality());
        p.setMaritalStatus(d.maritalStatus());
        p.setWorkAuthorization(MapperUtils.copy(d.workAuthorization()));
        p.setLinkedinUrl(d.linkedinUrl());
        p.setGithubUrl(d.githubUrl());
        p.setWebsiteUrl(d.websiteUrl());
    }

    /** Applique les règles du marché : photo et infos personnelles masquées si non autorisées. */
    public ProfileView toView(Profile p, Market market, ViewContext ctx) {
        boolean photo = market.isShowPhoto();
        boolean personal = market.isShowPersonalDetails();
        return new ProfileView(p.getFullName(), ctx.text(p.getHeadline()), ctx.text(p.getSummary()), p.getEmail(),
                p.getPhone(), p.getCity(), p.getCountry(),
                photo ? p.getPhotoUrl() : null,
                personal ? p.getBirthDate() : null,
                personal ? p.getNationality() : null,
                personal ? p.getMaritalStatus() : null,
                ctx.text(p.getWorkAuthorization()), p.getLinkedinUrl(), p.getGithubUrl(), p.getWebsiteUrl());
    }
}
