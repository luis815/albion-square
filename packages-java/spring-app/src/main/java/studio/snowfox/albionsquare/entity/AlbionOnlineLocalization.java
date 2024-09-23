package studio.snowfox.albionsquare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "albion_online_localization", schema = "albion_square")
public class AlbionOnlineLocalization {
    @Id
    @Column(name = "tuid", nullable = false, length = Integer.MAX_VALUE)
    private String tuid;

    @Column(name = "en_us", length = Integer.MAX_VALUE)
    private String enUs;

    @Column(name = "de_de", length = Integer.MAX_VALUE)
    private String deDe;

    @Column(name = "fr_fr", length = Integer.MAX_VALUE)
    private String frFr;

    @Column(name = "ru_ru", length = Integer.MAX_VALUE)
    private String ruRu;

    @Column(name = "pl_pl", length = Integer.MAX_VALUE)
    private String plPl;

    @Column(name = "es_es", length = Integer.MAX_VALUE)
    private String esEs;

    @Column(name = "pt_br", length = Integer.MAX_VALUE)
    private String ptBr;

    @Column(name = "it_it", length = Integer.MAX_VALUE)
    private String itIt;

    @Column(name = "zh_cn", length = Integer.MAX_VALUE)
    private String zhCn;

    @Column(name = "ko_kr", length = Integer.MAX_VALUE)
    private String koKr;

    @Column(name = "ja_jp", length = Integer.MAX_VALUE)
    private String jaJp;

    @Column(name = "zh_tw", length = Integer.MAX_VALUE)
    private String zhTw;

    @Column(name = "id_id", length = Integer.MAX_VALUE)
    private String idId;

    @Column(name = "tr_tr", length = Integer.MAX_VALUE)
    private String trTr;

    @Column(name = "ar_sa", length = Integer.MAX_VALUE)
    private String arSa;

    @NotNull
    @Column(name = "sha", nullable = false, length = Integer.MAX_VALUE)
    private String sha;

    @Column(
            name = "create_timestamp",
            columnDefinition = "TIMESTAMP WITH TIME ZONE",
            nullable = false,
            updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp updateTimestamp;
}
