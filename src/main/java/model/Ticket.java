package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

//import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ticket {

    private Long id;
    private LocalDateTime dateTime;
    private Long userId;
    private Long routeId;
    private BigDecimal price;
    private String seatNumber;
}
