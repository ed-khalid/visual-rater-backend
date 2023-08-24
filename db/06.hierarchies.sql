DROP TABLE IF EXISTS hierarchies CASCADE ;
CREATE TABLE hierarchies(
                      id uuid PRIMARY KEY,
                      name varchar(250) NOT NULL
);

DROP TABLE IF EXISTS hierarchy_level CASCADE;
CREATE TABLE hierarchy_level(id uuid PRIMARY KEY, order int4 NOT NULL, name varchar(250) NOT NULL, parentId uuid, hierarchyId uuid REFERENCES hierarchies);
CREATE TABLE hierarchy_level_parent_children(id uuid PRIMARY key, parentId uuid references hierarchy_level, childId uuid references hierarchy_level);
