DO ' DECLARE BEGIN IF NOT EXISTS(SELECT FROM artist_tier) THEN INSERT INTO artist_tier (id, value) VALUES (uuid_generate_v1(), ''A''), (uuid_generate_v1(), ''B''), (uuid_generate_v1(), ''C''), (uuid_generate_v1(), ''D''), (uuid_generate_v1(), ''E''), (uuid_generate_v1(), ''F''); END IF; END ' LANGUAGE plpgsql;