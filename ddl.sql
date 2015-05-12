

DROP TABLE users CASCADE;
DROP TABLE articles CASCADE;
DROP TABLE tags CASCADE;
DROP TABLE article_tags CASCADE;

CREATE TABLE users (
    id bigserial UNIQUE NOT NULL,
    nick_name varchar(128) NOT NULL,
    password varchar(128) DEFAULT '********',
    profile text,
    updated timestamp with timezone NOT NULL DEFAULT now(),
    created timestamp with timezone NOT NULL DEFAULT now(),
    PRIMARY KEY(id)
);

CREATE TABLE articles (
    id bigserial UNIQUE NOT NULL,
    title varchar(256) NOT NULL,
    contents text NOT NULL,
    owner_user_id bigint NOT NULL,
    updated timestamp with timezone NOT NULL DEFAULT now(),
    created timestamp with timezone NOT NULL DEFAULT now(),
    PRIMARY KEY(id)
);

CREATE TABLE tags (
    id bigserial UNIQUE NOT NULL,
    name varchar(256) UNIQUE NOT NULL,
    updated timestamp with timezone NOT NULL DEFAULT now(),
    created timestamp with timezone NOT NULL DEFAULT now(),
    PRIMARY KEY(id)
);

CREATE TABLE article_tags (
    article_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    updated timestamp with timezone NOT NULL DEFAULT now(),
    created timestamp with timezone NOT NULL DEFAULT now(),
    PRIMARY KEY(article_id, tag_id)
);
