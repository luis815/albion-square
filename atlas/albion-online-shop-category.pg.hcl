table "albion_online_shop_category" {
  schema = schema.albion_square

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
      column.shop_category,
      column.shop_sub_category
    ]
  }
}
