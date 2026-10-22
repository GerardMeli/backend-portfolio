package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.common.LocaleSupport;
import gerard.meli.backend_portfolio.profile.Profile;
import gerard.meli.backend_portfolio.profile.ProfileRepository;
import gerard.meli.backend_portfolio.profile.ProfileVariant;
import gerard.meli.backend_portfolio.profile.ProfileVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProfileService {

    private final ProfileRepository profiles;
    private final ProfileVariantRepository variants;
    private final AdminSupport support;
    private final LocaleSupport locales;

    @Transactional(readOnly = true)
    public ProfileResponse get() {
        return profiles.findFirstBy().map(this::toResponse).orElseThrow(() -> support.notFound("Profile"));
    }

    /** Crée le profil s'il n'existe pas encore, sinon le met à jour (un seul profil). */
    public ProfileResponse save(ProfileRequest r) {
        Profile p = profiles.findFirstBy().orElseGet(Profile::new);
        p.setFullName(r.fullName().trim());
        p.setEmail(r.email().trim());
        p.setPhone(r.phone());
        p.setLocation(r.location());
        p.setGithubUrl(r.githubUrl());
        p.setLinkedinUrl(r.linkedinUrl());
        p.setAvatarUrl(r.avatarUrl());
        support.syncTranslations(p.getTranslations(), r.translations(), t -> {
            Profile.Text text = new Profile.Text();
            text.setHeadline(t.headline());
            text.setBio(t.bio());
            return text;
        });
        return toResponse(profiles.save(p));
    }

    // ------------------------------------------------------------------ variantes marché

    @Transactional(readOnly = true)
    public List<VariantResponse> listVariants() {
        return variants.findAllByOrderByMarketAscLocaleAsc().stream().map(this::toVariant).toList();
    }

    public VariantResponse createVariant(VariantRequest r) {
        ProfileVariant v = new ProfileVariant();
        applyVariant(v, r);
        return toVariant(variants.saveAndFlush(v)); // flush : un doublon (marché + langue) renvoie 409 tout de suite
    }

    public VariantResponse updateVariant(UUID id, VariantRequest r) {
        ProfileVariant v = variants.findById(id).orElseThrow(() -> support.notFound("Variant"));
        applyVariant(v, r);
        return toVariant(variants.saveAndFlush(v));
    }

    public void deleteVariant(UUID id) {
        ProfileVariant v = variants.findById(id).orElseThrow(() -> support.notFound("Variant"));
        variants.delete(v);
    }

    private void applyVariant(ProfileVariant v, VariantRequest r) {
        if (!locales.isSupported(r.locale())) {
            throw support.badRequest("Unsupported locale: " + r.locale());
        }
        v.setMarket(r.market());
        v.setLocale(r.locale());
        v.setHeadline(r.headline());
        v.setSummary(r.summary());
        v.setCtaLabel(r.ctaLabel());
        v.setCvFileUrl(r.cvFileUrl());
        v.setActive(support.flag(r.active(), true));
    }

    // ------------------------------------------------------------------ mapping

    private ProfileResponse toResponse(Profile p) {
        return new ProfileResponse(p.getId(), p.getFullName(), p.getEmail(), p.getPhone(), p.getLocation(),
                p.getGithubUrl(), p.getLinkedinUrl(), p.getAvatarUrl(),
                AdminSupport.mapValues(p.getTranslations(), t -> new ProfileText(t.getHeadline(), t.getBio())));
    }

    private VariantResponse toVariant(ProfileVariant v) {
        return new VariantResponse(v.getId(), v.getMarket(), v.getLocale(), v.getHeadline(), v.getSummary(),
                v.getCtaLabel(), v.getCvFileUrl(), v.isActive());
    }
}