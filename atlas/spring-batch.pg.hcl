table "batch_job_execution" {
  schema = schema.albion_square

  column "job_execution_id" {
    null = false
    type = bigint
  }
  column "version" {
    null = true
    type = bigint
  }
  column "job_instance_id" {
    null = false
    type = bigint
  }
  column "create_time" {
    null = false
    type = timestamp
  }
  column "start_time" {
    null = true
    type = timestamp
  }
  column "end_time" {
    null = true
    type = timestamp
  }
  column "status" {
    null = true
    type = character_varying(10)
  }
  column "exit_code" {
    null = true
    type = character_varying(2500)
  }
  column "exit_message" {
    null = true
    type = character_varying(2500)
  }
  column "last_updated" {
    null = true
    type = timestamp
  }
  primary_key {
    columns = [column.job_execution_id]
  }
  foreign_key "job_inst_exec_fk" {
    columns     = [column.job_instance_id]
    ref_columns = [table.batch_job_instance.column.job_instance_id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
}

table "batch_job_execution_context" {
  schema = schema.albion_square

  column "job_execution_id" {
    null = false
    type = bigint
  }
  column "short_context" {
    null = false
    type = character_varying(2500)
  }
  column "serialized_context" {
    null = true
    type = text
  }
  primary_key {
    columns = [column.job_execution_id]
  }
  foreign_key "job_exec_ctx_fk" {
    columns     = [column.job_execution_id]
    ref_columns = [table.batch_job_execution.column.job_execution_id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
}

table "batch_job_execution_params" {
  schema = schema.albion_square

  column "job_execution_id" {
    null = false
    type = bigint
  }
  column "parameter_name" {
    null = false
    type = character_varying(100)
  }
  column "parameter_type" {
    null = false
    type = character_varying(100)
  }
  column "parameter_value" {
    null = true
    type = character_varying(2500)
  }
  column "identifying" {
    null = false
    type = character(1)
  }
  foreign_key "job_exec_params_fk" {
    columns     = [column.job_execution_id]
    ref_columns = [table.batch_job_execution.column.job_execution_id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
}

table "batch_job_instance" {
  schema = schema.albion_square

  column "job_instance_id" {
    null = false
    type = bigint
  }
  column "version" {
    null = true
    type = bigint
  }
  column "job_name" {
    null = false
    type = character_varying(100)
  }
  column "job_key" {
    null = false
    type = character_varying(32)
  }
  primary_key {
    columns = [column.job_instance_id]
  }
  unique "job_inst_un" {
    columns = [column.job_name, column.job_key]
  }
}

table "batch_step_execution" {
  schema = schema.albion_square

  column "step_execution_id" {
    null = false
    type = bigint
  }
  column "version" {
    null = false
    type = bigint
  }
  column "step_name" {
    null = false
    type = character_varying(100)
  }
  column "job_execution_id" {
    null = false
    type = bigint
  }
  column "create_time" {
    null = false
    type = timestamp
  }
  column "start_time" {
    null = true
    type = timestamp
  }
  column "end_time" {
    null = true
    type = timestamp
  }
  column "status" {
    null = true
    type = character_varying(10)
  }
  column "commit_count" {
    null = true
    type = bigint
  }
  column "read_count" {
    null = true
    type = bigint
  }
  column "filter_count" {
    null = true
    type = bigint
  }
  column "write_count" {
    null = true
    type = bigint
  }
  column "read_skip_count" {
    null = true
    type = bigint
  }
  column "write_skip_count" {
    null = true
    type = bigint
  }
  column "process_skip_count" {
    null = true
    type = bigint
  }
  column "rollback_count" {
    null = true
    type = bigint
  }
  column "exit_code" {
    null = true
    type = character_varying(2500)
  }
  column "exit_message" {
    null = true
    type = character_varying(2500)
  }
  column "last_updated" {
    null = true
    type = timestamp
  }
  primary_key {
    columns = [column.step_execution_id]
  }
  foreign_key "job_exec_step_fk" {
    columns     = [column.job_execution_id]
    ref_columns = [table.batch_job_execution.column.job_execution_id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
}

table "batch_step_execution_context" {
  schema = schema.albion_square

  column "step_execution_id" {
    null = false
    type = bigint
  }
  column "short_context" {
    null = false
    type = character_varying(2500)
  }
  column "serialized_context" {
    null = true
    type = text
  }
  primary_key {
    columns = [column.step_execution_id]
  }
  foreign_key "step_exec_ctx_fk" {
    columns     = [column.step_execution_id]
    ref_columns = [table.batch_step_execution.column.step_execution_id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
}

// The following are not supported, but are required
//
// sequence "batch_step_execution_seq" {
//   schema = schema.albion_square
//   max_value = 9223372036854775807
// }
//
// sequence "batch_job_execution_seq" {
//   schema = schema.albion_square
//   max_value = 9223372036854775807
// }
//
// sequence "batch_job_seq" {
//   schema = schema.albion_square
//   max_value = 9223372036854775807
// }
