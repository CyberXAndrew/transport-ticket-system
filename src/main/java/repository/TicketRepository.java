package repository;

import dto.TicketDTO;
import model.Ticket;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository {
    //        c. Покупка определенного билета. Уже купленный билет не должен быть повторно доступен для покупки.
    //        d. Получение списка всех купленных билетов для текущего пользователя.

    Ticket findById(Long ticketId);

    List<TicketDTO> findAll();
    List<TicketDTO> findAll(Pageable pageable,
                            LocalDateTime dateTime,
                            String departurePoint,
                            String destinationPoint,
                            String carrierName);     //        b. Получение списка всех доступных для покупки билетов, с возможностью пагинации (страница/размер) и фильтрации по следующим атрибутам:
    //            i. Дата/время
    //            ii. Пункты отправления/назначения (вхождение по строке).
    //            iii. Название перевозчика (вхождение по строке).

    List<Ticket> findByUserId(Long userId);

    Ticket save(Ticket ticket);

    Ticket update(Ticket ticket);

    void deleteById(Long ticketId);

    boolean isTicketAvailable(Long ticketId);
}
