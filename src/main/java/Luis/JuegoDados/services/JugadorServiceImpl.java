package Luis.JuegoDados.services;

import Luis.JuegoDados.dto.JugadorDto;
import Luis.JuegoDados.dto.PromedioJugadorDto;
import Luis.JuegoDados.entity.JugadorEntity;
import Luis.JuegoDados.excepciones.EmptyPlayersListException;
import Luis.JuegoDados.excepciones.PlayerNotFoundException;
import Luis.JuegoDados.excepciones.PlayerNotSavedException;
import Luis.JuegoDados.helper.FromEntityToDtoConverter;
import Luis.JuegoDados.repository.JugadorRepository;
import Luis.JuegoDados.repository.PartidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JugadorServiceImpl implements JugadorService{
    private static final Logger log = LoggerFactory.getLogger(JugadorServiceImpl.class);
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private FromEntityToDtoConverter converter;

    @Override
    public JugadorDto createNewPLayer(String nombre) throws PlayerNotSavedException {
        try{
            log.info("Iniciando el método createNewPlayer con nombre: {}", nombre);
            JugadorEntity nuevo = JugadorEntity.builder()
                    .nombreJugador(nombre)
                    .porcentajeExito(0)
                    .build();

            jugadorRepository.save(nuevo);
            log.info("Jugador creado exitosamente: {}", nuevo);

            JugadorDto jugadorDto = converter.convertirJugadorEntityADto(nuevo);
            log.info("La entidad se ha convertido a Dto correctamente: {}", jugadorDto);
            return jugadorDto;

        }catch (Exception e){
            log.error("Error al momento de crear un nuevo jugador " + e.getMessage());
            throw new PlayerNotSavedException("No se pudo guardar el jugador. Detalles del error: " + e.getMessage());
        }
    }

    @Override
    public JugadorDto updatePLayer(Long id, String nombre) {
        log.info("Buscando un jugador el la base de datos con el id " + id);
        JugadorEntity updatedPLayer = findPLayerById(id);

        log.info("jugador encontrado en la base de datos");
        String oldName = updatedPLayer.getNombreJugador();
        updatedPLayer.setNombreJugador(nombre);
        jugadorRepository.save(updatedPLayer);
        log.info("Nombre de jugador actualizado correctamente: nombre antiguo: " + oldName + "/nuevo nombre: " + nombre);

        return converter.convertirJugadorEntityADto(updatedPLayer);

    }

    @Override
    public JugadorEntity findPLayerById(Long id) {
        log.info("Buscando un jugador el la base de datos con el id " + id);
        return jugadorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se encontró ningún jugador con el ID " + id);
                    return new PlayerNotFoundException(id);
                });
    }

    @Override
    public List<PromedioJugadorDto> findAllPLayers() {
        try{
            List<JugadorEntity> players = jugadorRepository.findAll();
            return players.stream()
                    .map(jugador -> converter.convertirJugadorEntityAPromedioJugadorDto(jugador))
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.error("Error al recuperar la lista de jugadores: " + e.getMessage());
            throw new EmptyPlayersListException("Error al momento de recuperar la lista de jugadores " + e);
        }
    }

    @Override
    public int averagePlayerWins() {
        try{
            List<JugadorEntity> players = jugadorRepository.findAll();
            int totalJugadores = players.size();
            int promedioVictoriasGlobales = 0;

            if(totalJugadores == 0){
                return 0;
            }

            for (JugadorEntity player : players) {
                promedioVictoriasGlobales += player.getPorcentajeExito();
            }

            return promedioVictoriasGlobales / totalJugadores;

        }catch (Exception e){
            log.error("Error al recuperar la lista de jugadores: " + e.getMessage());
            throw new EmptyPlayersListException("Error al momento de recuperar la lista de jugadores " + e);
        }
    }

    @Override
    public List<PromedioJugadorDto> lowestScores() {
        List<JugadorEntity> todosLosJugadores = jugadorRepository.findAll();
        List<PromedioJugadorDto> peoresJugadores = new ArrayList<>();
        int porcentajeMasBajo = 100;

        if (todosLosJugadores.isEmpty()) {
            throw  new EmptyPlayersListException("Lista de jugadores vacía");
        }

        for (JugadorEntity jugador : todosLosJugadores) {
            int miPorcentajeDeExito = jugador.getPorcentajeExito();

            if(miPorcentajeDeExito < porcentajeMasBajo){
                peoresJugadores.clear();
                porcentajeMasBajo = miPorcentajeDeExito;
            }
            if (miPorcentajeDeExito == porcentajeMasBajo) {
                PromedioJugadorDto promedioJugadorDto = converter.convertirJugadorEntityAPromedioJugadorDto(jugador);
                peoresJugadores.add(promedioJugadorDto);
            }
        }
        return peoresJugadores;
    }

    @Override
    public List<PromedioJugadorDto> bestScores() {
        List<JugadorEntity> todosLosJugadores = jugadorRepository.findAll();
        List<PromedioJugadorDto> mejoresJugadores = new ArrayList<>();
        int porcentajeMasAlto = 0;

        if (todosLosJugadores.isEmpty()) {
            throw new EmptyPlayersListException("Lista de jugadores vacía");
        }

        for (JugadorEntity jugador : todosLosJugadores) {
            int miPorcentajeDeExito = jugador.getPorcentajeExito();

            if (miPorcentajeDeExito > porcentajeMasAlto) {
                mejoresJugadores.clear();
                porcentajeMasAlto = miPorcentajeDeExito;
            }
            if (miPorcentajeDeExito == porcentajeMasAlto) {
                PromedioJugadorDto jugadorDto = converter.convertirJugadorEntityAPromedioJugadorDto(jugador);
                mejoresJugadores.add(jugadorDto);
            }
        }

        return mejoresJugadores;
    }
}
