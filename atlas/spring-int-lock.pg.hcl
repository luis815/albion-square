table "int_lock" {
  schema = schema.albion_square

  column "lock_key" {
    type = char(36)
  }
  column "region" {
    type = varchar(100)
  }
  column "client_id" {
    type = char(36)
  }
  column "created_date" {
    null = false
    type = timestamp
  }
  primary_key {
    columns = [
      column.lock_key,
      column.region
    ]
  }
}
