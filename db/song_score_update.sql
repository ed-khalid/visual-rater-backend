DROP FUNCTION IF EXISTS recalculate_artist_song_metadata CASCADE;
CREATE FUNCTION recalculate_artist_song_metadata()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $rec$
DECLARE album_score double precision;
DECLARE artist_score double precision;
DECLARE classic$ int;
DECLARE great$ int;
DECLARE good$ int;
DECLARE mediocre$ int;
DECLARE bad$ int;
DECLARE terrible$ int;
DECLARE total$ int;
BEGIN
        select avg(score) from SONG s where s.album_id = NEW.album_id INTO album_score;
        UPDATE album SET score = album_score WHERE id = NEW.album_id;
        select avg(score) from album a where a.artist_id = NEW.artist_id INTO artist_score;
        UPDATE artist SET score = artist_score WHERE id = NEW.artist_id;
        select count(*) from song s where s.artist_id = NEW.artist_id and s.score >= 4.25 INTO classic$ ;
        select count(*) from song s where s.artist_id = NEW.artist_id and s.score >= 4 and s.score < 4.25 INTO great$ ;
        select count(*) from song s where s.artist_id = NEW.artist_id and s.score >= 3.5 and s.score < 4 INTO good$ ;
        select count(*) from song s where s.artist_id = NEW.artist_id and s.score >= 3 and s.score < 3.5 INTO mediocre$ ;
        select count(*) from song s where s.artist_id = NEW.artist_id and s.score >= 2 and s.score < 3 INTO bad$ ;
        select count(*) from song s where s.artist_id = NEW.artist_id and s.score < 2 INTO terrible$ ;
        select count(*) from song s where s.artist_id = NEW.artist_id INTO total$;
        update artist_metadata set total_songs = total$, bad = bad$, good = good$, great= great$, classic = classic$, terrible = terrible$, mediocre = mediocre$ where id =(select metadata_id from artist where artist.id = NEW.artist_id);
        RETURN NULL;
    END
$rec$;



DROP FUNCTION IF EXISTS recalculate_tier CASCADE;
CREATE FUNCTION recalculate_tier()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $tier$
    BEGIN
        CREATE TEMPORARY TABLE metadata AS
        SELECT m.*
        FROM artist_metadata m
        inner join artist a
        on a.metadata_id = m.id
        WHERE a.id = NEW.artist_id;
        RETURN NULL;
    end;
$tier$;



DROP FUNCTION IF EXISTS recalculate_total_albums CASCADE;
CREATE FUNCTION recalculate_total_albums()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $tot_alb$
    BEGIN
        update artist_metadata set total_albums = (select count(*) from album a WHERE a.artist_id = NEW.artist_id) ;
        RETURN NULL;
    end;
$tot_alb$;

CREATE TRIGGER trigger_album_total_recalc
    AFTER INSERT
    ON album
    EXECUTE PROCEDURE recalculate_total_albums();



CREATE TRIGGER trigger_recalc
    AFTER INSERT OR UPDATE
    ON song
    FOR EACH ROW
    EXECUTE PROCEDURE recalculate_artist_song_metadata();
