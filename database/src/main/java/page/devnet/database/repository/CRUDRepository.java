package page.devnet.database.repository;

import java.util.Optional;

public interface CRUDRepository<ID, ENTITY> {

    Optional<ENTITY> find(ID id);


    ENTITY createOrUpdate(ID id, ENTITY entity);

    ENTITY delete(ID id);
}
