table "albion_online_spell" {
  schema = schema.albion_square

  column "unique_name" {
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
      column.unique_name
    ]
  }
}
