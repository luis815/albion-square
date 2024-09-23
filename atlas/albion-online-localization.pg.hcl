table "albion_online_localization" {
    schema = schema.albion_square

    column "tuid" {
        null = false
        type = varchar
    }
    column "en_us" {
        null = true
        type = varchar
    }
    column "de_de" {
        null = true
        type = varchar
    }
    column "fr_fr" {
        null = true
        type = varchar
    }
    column "ru_ru" {
        null = true
        type = varchar
    }
    column "pl_pl" {
        null = true
        type = varchar
    }
    column "es_es" {
        null = true
        type = varchar
    }
    column "pt_br" {
        null = true
        type = varchar
    }
    column "it_it" {
        null = true
        type = varchar
    }
    column "zh_cn" {
        null = true
        type = varchar
    }
    column "ko_kr" {
        null = true
        type = varchar
    }
    column "ja_jp" {
        null = true
        type = varchar
    }
    column "zh_tw" {
        null = true
        type = varchar
    }
    column "id_id" {
        null = true
        type = varchar
    }
    column "tr_tr" {
        null = true
        type = varchar
    }
    column "ar_sa" {
        null = true
        type = varchar
    }
    column "sha" {
        null = false
        type = varchar
    }
    column "create_timestamp" {
        null = false
        type = timestamptz
    }
    column "update_timestamp" {
        null = false
        type = timestamptz
    }
    primary_key {
        columns = [
            column.tuid
        ]
    }
}
