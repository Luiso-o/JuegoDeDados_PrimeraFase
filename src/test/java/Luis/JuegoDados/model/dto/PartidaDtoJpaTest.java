package Luis.JuegoDados.model.dto;

import Luis.JuegoDados.dto.PartidaDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PartidaDtoJpaTest {

    @Test
    void testCrearPartidaDto() {
        PartidaDto partidaDto = PartidaDto.builder()
                .id(1L)
                .fecha(LocalDate.of(2023, 8, 11))
                .mensaje("Ganaste :D")
                .build();

        assertEquals(1L, partidaDto.getId());
        assertEquals(LocalDate.of(2023, 8, 11), partidaDto.getFecha());
        assertEquals("Ganaste :D", partidaDto.getMensaje());
    }

    @Test
    void testCrearPartidaDtoConBuilder() {
        PartidaDto partidaDto = PartidaDto.builder()
                .id(2L)
                .fecha(LocalDate.of(2023, 8, 15))
                .mensaje("Perdiste :v")
                .build();

        assertEquals(2L, partidaDto.getId());
        assertEquals(LocalDate.of(2023, 8, 15), partidaDto.getFecha());
        assertEquals("Perdiste :v", partidaDto.getMensaje());
    }

}