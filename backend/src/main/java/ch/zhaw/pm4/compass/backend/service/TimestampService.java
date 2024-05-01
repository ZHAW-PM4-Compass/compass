package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TimestampService {

    @Autowired
    private TimestampRepository timestampRepository;

    @Autowired
    private DaySheetRepository daySheetRepository;


    public TimestampDto createTimestamp(TimestampDto createTimestamp, String user_id) {
        Timestamp timestamp = convertTimestampDtoToDTimestamp(createTimestamp, user_id);
        if (timestamp != null && checkNoDoubleEntry(timestamp)) {
            return convertTimestampToTimestampDto(timestampRepository.save(timestamp));
        } else {
            return null;
        }
    }

    public TimestampDto getTimestampById(Long id, String user_id) {
        Optional<Timestamp> response = timestampRepository.findById(id);
        if (response.isEmpty())
            return null;
        if (response.get().getUserId().equals(user_id))
            return convertTimestampToTimestampDto(response.get());
        return null;
    }

    public ArrayList<TimestampDto> getAllTimestampsByDaySheetId(Long id, String userId) {
        ArrayList<TimestampDto> resultList = new ArrayList<>();
        Iterable<Timestamp> list = timestampRepository.findAllByDaySheetIdAndUserId(id, userId);
        for (Timestamp timestamp : list)
            resultList.add(convertTimestampToTimestampDto(timestamp));

        return resultList;
    }

    public TimestampDto updateTimestampById(TimestampDto updateTimestampDto, String userId) {
        Optional<Timestamp> response = timestampRepository.findByIdAndUserId(updateTimestampDto.getId(), userId);
        if (response.isPresent()) {
            Timestamp timestamp = response.get();
            Timestamp newTimestamp = new Timestamp(timestamp.getId(), timestamp.getDaySheet(), updateTimestampDto.getStart_time(), updateTimestampDto.getStart_time());
            if (checkNoDoubleEntry(newTimestamp)) {
                timestamp.setStartTime(updateTimestampDto.getStart_time());
                timestamp.setEndTime(updateTimestampDto.getEnd_time());
                return convertTimestampToTimestampDto(timestampRepository.save(timestamp));
            }
        }

        return null;

    }

    public void deleteTimestamp(Long id, String userId) {
        Optional<Timestamp> timestamp = timestampRepository.findByIdAndUserId(id, userId);
        if (timestamp.isPresent())
            timestampRepository.delete(timestamp.get());
    }

    private Timestamp convertTimestampDtoToDTimestamp(TimestampDto timestampDto, String user_id) {
        Optional<DaySheet> option = daySheetRepository.findByIdAndUserId(timestampDto.getDay_sheet_id(), user_id);
        if (option.isPresent())
            return new Timestamp(timestampDto.getId(), option.get(), timestampDto.getStart_time(), timestampDto.getEnd_time(), user_id);
        return null;
    }

    public TimestampDto convertTimestampToTimestampDto(Timestamp timestamp) {
        return new TimestampDto(timestamp.getId(), timestamp.getDaySheet().getId(), timestamp.getStartTime(), timestamp.getEndTime());
    }

    private boolean checkNoDoubleEntry(Timestamp timestampToCheck) {
        Iterable<Timestamp> timestamps = timestampRepository.findAllByDaySheetIdAndUserId(timestampToCheck.getDaySheet().getId(), timestampToCheck.getUserId());

        boolean noDoubleEntry = true;
        for (Timestamp timestamp : timestamps) {
            if (timestampToCheck.getStartTime().before(timestamp.getEndTime())
                    && timestampToCheck.getStartTime().after(timestamp.getStartTime())) {
                noDoubleEntry = false;
                break;
            }

            if (timestampToCheck.getEndTime().before(timestamp.getEndTime())
                    && timestampToCheck.getEndTime().after(timestamp.getStartTime())) {
                noDoubleEntry = false;
                break;
            }

            if (timestampToCheck.getStartTime().equals(timestamp.getStartTime())
                    && timestampToCheck.getEndTime().equals(timestamp.getEndTime())) {
                noDoubleEntry = false;
                break;
            }
            if (timestampToCheck.getStartTime().before(timestamp.getStartTime())
                    && timestampToCheck.getEndTime().after(timestamp.getEndTime())) {
                noDoubleEntry = false;
                break;
            }
        }
        return noDoubleEntry;
    }
}
