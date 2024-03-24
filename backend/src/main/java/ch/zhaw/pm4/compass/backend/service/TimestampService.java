package ch.zhaw.pm4.compass.backend.service;


import ch.zhaw.pm4.compass.backend.exception.TimestampNotFoundException;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.repository.DayRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimestampService {

    @Autowired
    private TimestampRepository timestampRepository;
    @Autowired
    private DayRepository dayRepository;
    public Timestamp createTimestamp(Timestamp createTimestamp,Long day_id) {
        //User user = convertToUser(createUser);
        //Boolean complete = checkIfComplete(user);
        //user.setComplete(complete);
        //return convertToGetDTO(userRepository.save(user));
        createTimestamp.setDay(dayRepository.findById(day_id).get());
        return timestampRepository.save(createTimestamp);
    }

    public Timestamp getTimestampById(Long id) throws TimestampNotFoundException {
        //return convertToGetDTO(userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException(id)));
        return timestampRepository.findById(id).orElseThrow(() -> new TimestampNotFoundException(id));
    }

    public Timestamp updateTimestampById(Timestamp updateTimestamp) throws TimestampNotFoundException{
        return timestampRepository.save(updateTimestamp);
    }

    public void deleteTimestamp(Long id){
        timestampRepository.deleteById(id);
    }



}
