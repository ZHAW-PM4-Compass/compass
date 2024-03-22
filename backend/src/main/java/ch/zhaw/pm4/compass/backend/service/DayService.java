package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.exception.DayNotFoundException;
import ch.zhaw.pm4.compass.backend.model.Day;
import ch.zhaw.pm4.compass.backend.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DayService {

    @Autowired
    private DayRepository dayRepository;

    public Day createDay(Day createDay) {
        //User user = convertToUser(createUser);
        //Boolean complete = checkIfComplete(user);
        //user.setComplete(complete);
        //return convertToGetDTO(userRepository.save(user));
        return dayRepository.save(createDay);
    }

    public Day getDayById(Long id) throws DayNotFoundException {
        //return convertToGetDTO(userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException(id)));
        return dayRepository.getDayById(id).orElseThrow(() -> new DayNotFoundException(id));
    }
}
