create table public.task (
    task_id serial primary key,
    description varchar(100) not null,
    done boolean not null
);