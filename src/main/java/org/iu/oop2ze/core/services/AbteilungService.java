package org.iu.oop2ze.core.services;

import lombok.extern.slf4j.Slf4j;
import org.iu.oop2ze.core.database.models.Abteilung;
import org.iu.oop2ze.core.database.models.Mitarbeiter;
import org.iu.oop2ze.core.database.repositories.AbteilungRepository;
import org.iu.oop2ze.core.services.interfaces.IAbteilungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Klasse, welche Funktionen und Methoden für Abteilungen beinhaltet
 *
 * @author Julius Beier
 */
@Service
@Slf4j
public class AbteilungService implements IAbteilungService {
    @Autowired
    private AbteilungRepository abteilungRepository;

    @Override
    public Abteilung erstelleAbteilung(final String name, final Boolean isHr, final Mitarbeiter leitenderMitarbeiter) {
        if (name.isBlank() || leitenderMitarbeiter == null || isHr == null) {
            throw new IllegalArgumentException(); //hier was mitgeben wie "Attribute unausreichend
        }                                           // Abteilung kann nicht erstellt werden"

        //TODO das hier kracht schon in der Anweisung, mach nen Optional<Abteilung> draus und prüf dann isPresent()
        if (abteilungRepository.findByName(name) != null) {
            log.error("Abteilung mit dem Namen: '%s' existiert bereits".formatted(name));
            return null;
        }

        //unschön, lieber Abteilung abteilung = new Abteilung(..)
        var abteilung = new Abteilung(name, isHr, leitenderMitarbeiter);

        //das repo schmeißt nachm save immer das gespeicherte objekt zurück kannst dir also die
        // letzte zeile klemmen und das return vor den repo aufruf schreiben
        abteilungRepository.save(abteilung);
        return abteilung;
    }

    //TODO mit Objekten direkt überreichen, dann Änderungen vornehmen und persistieren wäre ich vorsichtig
    //TODO übergib lieber die id und ziehs dir neu ausm repo sonst könntest du hier
    // mal ne detached Entity to persist exception kriegen
    @Override
    public Abteilung bearbeiteAbteilung(
            Abteilung abteilung,
            final String name,
            final Mitarbeiter leitenderMitarbeiter
    ) {
        if (abteilung == null) {
            throw new IllegalArgumentException();
        }

        if (!name.isBlank())
            abteilung.setName(name);

        if (leitenderMitarbeiter != null)
            abteilung.setLeitenderMitarbeiter(leitenderMitarbeiter);

        //siehe Zeile41
        abteilungRepository.save(abteilung);
        return abteilung;
    }

    //TODO hier das gleiche übergib die ID und ermittle Abteilung durch repo bzw schreib dir ne
    // kleine hilfsmethode "getAbteilungById(final Long id)"
    @Override
    public void loescheAbteilung(final Abteilung abteilung) {
        if (abteilung == null) {
            throw new IllegalArgumentException();
        }

        abteilungRepository.delete(abteilung);
    }

    @Override
    public List<Abteilung> findeAlle() {
        //TODO mach das in python hier wird gefällig ordentlich deklariert!
        var abteilungen = new ArrayList<Abteilung>(); // List<Abteilungen> abteilungen = new ArrayList<Abteilungen>(); !!!!!
        abteilungRepository.findAll().forEach(abteilungen::add);
        return abteilungen;
    }

    /**
     * Findet Abteilungen mit dem übergebenen Namen
     *
     * @param name Name der Abteilung nach welcher gesucht wird
     * @return Die gesuchte Abteilung
     * @author Julius Beier
     */
    public Abteilung findeAbteilungNachName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException();
        }

        //TODO ... Z84, Z32
        var abteilung = abteilungRepository.findByName(name);

        if (abteilung == null) {
            log.error("Abteilung mit dem Namen: '%s' konnte nicht gefunden werden".formatted(name));
            return null;
        }

        return abteilung;
    }
}
