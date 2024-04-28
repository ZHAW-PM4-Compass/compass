package ch.zhaw.pm4.compass.backend.service;


import ch.zhaw.pm4.compass.backend.exception.TimestampNotFoundException;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.CreateTimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.GetTimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateTimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimestampService {

    @Autowired
    private TimestampRepository timestampRepository;

    @Autowired
    private DaySheetRepository daySheetRepository;


    public GetTimestampDto createTimestamp(CreateTimestampDto createTimestamp) {
        Timestamp timestamp = convertCreateTimestampDtoToDay(createTimestamp);
        //LocalUser user = convertToUser(createUser);
        //Boolean complete = checkIfComplete(user);
        //user.setComplete(complete);
        //return convertToGetDTO(userRepository.save(user));
        return convertTimeToGetTimestampDto(timestampRepository.save(timestamp));
    }

    public GetTimestampDto getTimestampById(Long id) throws TimestampNotFoundException {
        //return convertToGetDTO(userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException(id)));
        return convertTimeToGetTimestampDto(timestampRepository.findById(id).orElseThrow(() -> new TimestampNotFoundException(id)));
    }

    public GetTimestampDto updateTimestampById(UpdateTimestampDto updateTimestampDto) throws TimestampNotFoundException {
        Timestamp timestamp = timestampRepository.getById(updateTimestampDto.getId());
        timestamp.setStartTime(updateTimestampDto.getStart_time());
        timestamp.setEndTime(updateTimestampDto.getEnd_time());
        DaySheet daySheet = daySheetRepository.findById(updateTimestampDto.getDay_sheet_id()).get();
        timestamp.setDaySheet(daySheet);
        return convertTimeToGetTimestampDto(timestampRepository.save(timestamp));
    }

    public void deleteTimestamp(Long id) {
        timestampRepository.deleteById(id);
    }

    private Timestamp convertCreateTimestampDtoToDay(CreateTimestampDto timestampDto) {
        DaySheet daySheet = daySheetRepository.findById(timestampDto.getDay_sheet_id()).get();
        return new Timestamp(daySheet, timestampDto.getStart_time(), timestampDto.getEnd_time());
    }

    private GetTimestampDto convertTimeToGetTimestampDto(Timestamp timestamp) {
        return new GetTimestampDto(timestamp.getId(), timestamp.getDaySheet().getId(), timestamp.getStartTime(), timestamp.getEndTime());
    }
}
