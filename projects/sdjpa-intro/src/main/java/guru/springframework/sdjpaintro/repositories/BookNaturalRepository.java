package guru.springframework.sdjpaintro.repositories;

import guru.springframework.sdjpaintro.domain.BookNatural;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookNaturalRepository extends JpaRepository<BookNatural, String> {
}
