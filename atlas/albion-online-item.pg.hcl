table "albion_online_item" {
    schema = schema.albion_square

    column "unique_name" {
        null = false
        type = varchar
    }
    column "tier" {
        null = false
        type = smallint
    }
    column "enchantment_level_0" {
            null = false
            type = boolean
        }
    column "enchantment_level_1" {
        null = false
        type = boolean
    }
    column "enchantment_level_2" {
        null = false
        type = boolean
    }
    column "enchantment_level_3" {
        null = false
        type = boolean
    }
    column "enchantment_level_4" {
        null = false
        type = boolean
    }
    column "max_quality" {
        null = false
        type = smallint
    }
    column "shop_category" {
        null = false
        type = varchar
    }
    column "shop_sub_category" {
        null = false
        type = varchar
    }
    column "sha" {
        null = false
        type = varchar
    }
    column "asset" {
        null = true
        type = boolean
    }
    column "asset_src" {
        null = true
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
            column.unique_name
        ]
    }
}
