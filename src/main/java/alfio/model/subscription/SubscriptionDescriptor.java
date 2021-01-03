/**
 * This file is part of alf.io.
 *
 * alf.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alf.io is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alf.io.  If not, see <http://www.gnu.org/licenses/>.
 */
package alfio.model.subscription;

import alfio.manager.system.ConfigurationLevel;
import alfio.model.ContentLanguage;
import alfio.model.Event;
import alfio.model.LocalizedContent;
import alfio.model.PriceContainer.VatStatus;
import alfio.model.Purchasable;
import alfio.model.support.Array;
import alfio.model.support.JSONData;
import alfio.model.transaction.PaymentProxy;
import ch.digitalfondue.npjt.ConstructorAnnotationRowMapper.Column;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class SubscriptionDescriptor implements Purchasable, LocalizedContent {



    public enum SubscriptionUsageType {
        ONCE_PER_EVENT, UNLIMITED
    }

    public enum SubscriptionTimeUnit {
        DAYS, MONTHS, YEARS
    }

    public enum SubscriptionValidityType {
        STANDARD, CUSTOM, NOT_SET
    }

    private final UUID id;
    private final Map<String, String> title;
    private final Map<String, String> description;
    private final int maxAvailable;
    private final ZonedDateTime creation;
    private final ZonedDateTime onSaleFrom;
    private final ZonedDateTime onSaleTo;
    private final int price;
    private final BigDecimal vat;
    private final VatStatus vatStatus;
    private final String currency;
    private final boolean isPublic;
    private final int organizationId;

    private final int maxEntries;
    private final SubscriptionValidityType validityType;
    private final SubscriptionTimeUnit validityTimeUnit;
    private final Integer validityUnits;
    private final ZonedDateTime validityFrom;
    private final ZonedDateTime validityTo;
    private final SubscriptionUsageType usageType;

    private final String termsAndConditionsUrl;
    private final String privacyPolicyUrl;
    private final String fileBlobId;
    private final List<PaymentProxy> paymentProxies;
    private final UUID privateKey;

    public SubscriptionDescriptor(@Column("id") UUID id,
                                  @Column("title") @JSONData Map<String, String> title,
                                  @Column("description") @JSONData Map<String, String> description,
                                  @Column("max_available") int maxAvailable,
                                  @Column("creation_ts") ZonedDateTime creation,
                                  @Column("on_sale_from") ZonedDateTime onSaleFrom,
                                  @Column("on_sale_to") ZonedDateTime onSaleTo,
                                  @Column("price_cts") int price,
                                  @Column("vat") BigDecimal vat,
                                  @Column("vat_status") VatStatus vatStatus,
                                  @Column("currency") String currency,
                                  @Column("is_public") boolean isPublic,
                                  @Column("organization_id_fk") int organizationId,

                                  @Column("max_entries") int maxEntries,
                                  @Column("validity_type") SubscriptionValidityType validityType,
                                  @Column("validity_time_unit") SubscriptionTimeUnit validityTimeUnit,
                                  @Column("validity_units") Integer validityUnits,
                                  @Column("validity_from") ZonedDateTime validityFrom,
                                  @Column("validity_to") ZonedDateTime validityTo,
                                  @Column("usage_type") SubscriptionUsageType usageType,

                                  @Column("terms_conditions_url") String termsAndConditionsUrl,
                                  @Column("privacy_policy_url") String privacyPolicyUrl,
                                  @Column("file_blob_id_fk") String fileBlobId,
                                  @Column("allowed_payment_proxies") @Array List<String> paymentProxies,
                                  @Column("private_key") UUID privateKey) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.maxAvailable = maxAvailable;
        this.creation = creation;
        this.onSaleFrom = onSaleFrom;
        this.onSaleTo = onSaleTo;
        this.price = price;
        this.vat = vat;
        this.vatStatus = vatStatus;
        this.currency = currency;
        this.isPublic = isPublic;
        this.organizationId = organizationId;

        this.maxEntries = maxEntries;
        this.validityType = validityType;
        this.validityTimeUnit = validityTimeUnit;
        this.validityUnits = validityUnits;
        this.validityFrom = validityFrom;
        this.validityTo = validityTo;
        this.usageType = usageType;

        this.termsAndConditionsUrl = termsAndConditionsUrl;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.fileBlobId = fileBlobId;
        this.paymentProxies = paymentProxies.stream().map(PaymentProxy::valueOf).collect(Collectors.toList());

        this.privateKey = privateKey;
    }

    @Override
    public List<ContentLanguage> getContentLanguages() {
        var languages = title.keySet();
        return ContentLanguage.ALL_LANGUAGES.stream()
            .filter(l -> languages.contains(l.getLanguage()))
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public ConfigurationLevel getConfigurationLevel() {
        return ConfigurationLevel.organization(organizationId);
    }

    @Override
    public List<PaymentProxy> getAllowedPaymentProxies() {
        return getPaymentProxies();
    }

    @Override
    public String getPrivacyPolicyLinkOrNull() {
        return StringUtils.trimToNull(privacyPolicyUrl);
    }

    @Override
    public String getPublicIdentifier() {
        return getId().toString();
    }

    @JsonIgnore
    @Override
    public PurchasableType getType() {
        return PurchasableType.SUBSCRIPTION;
    }

    @JsonIgnore
    @Override
    public ZoneId getZoneId() {
        return ZoneId.of("UTC");
    }

    @Override
    public String getDisplayName() {
        return title.keySet().stream().findFirst().map(title::get).orElse("Subscription"); //FIXME
    }

    @JsonIgnore
    @Override
    public Optional<Event> event() {
        return Optional.empty();
    }

    @JsonIgnore
    @Override
    public String getPrivateKey() {
        return privateKey.toString();
    }
}
