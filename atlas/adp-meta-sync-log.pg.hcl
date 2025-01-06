table "adp_meta_sync_log" {
    schema = schema.albion_square

    column "id" {
        null = false
        type = bigint
        identity {
            generated = ALWAYS
        }
    }
    column "sha" {
        null = false
        type = varchar
    }
    column "status" {
        null = false
        type = enum.generic_status
    }
    column "raw_adp_items" {
        null = true
        type = xml
    }
    column "raw_adp_tmx" {
        null = true
        type = xml
    }
    column "raw_adp_spells" {
        null = true
        type = xml
    }
    column "description" {
        null = true
        type = text
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
            column.id
        ]
    }
    unique "unique_sha" {
        columns = [column.sha]
    }
}
