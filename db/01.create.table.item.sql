
DROP TABLE IF EXISTS items CASCADE ;
CREATE TABLE items(
  id uuid PRIMARY KEY,
  name varchar(250) NOT NULL,
  type_id INTEGER references lu_item_types(id)
);
