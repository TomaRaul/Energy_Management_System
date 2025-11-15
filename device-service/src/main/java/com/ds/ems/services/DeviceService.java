package com.ds.ems.services;


import com.ds.ems.dtos.DeviceDTO;
import com.ds.ems.dtos.DeviceDetailsDTO;
import com.ds.ems.dtos.builders.DeviceBuilder;
import com.ds.ems.handlers.exceptions.model.ResourceNotFoundException;
import com.ds.ems.repositories.DeviceRepository;
import com.ds.ems.entities.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository DeviceRepository;

    @Autowired
    public DeviceService(DeviceRepository DeviceRepository) {
        this.DeviceRepository = DeviceRepository;
    }

    public List<DeviceDTO> findDevice() {
        List<Device> DeviceList = DeviceRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return DeviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(Integer id) {
        Optional<Device> prosumerOptional = DeviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public List<DeviceDTO> findDeviceByUserId(Integer user_id ) {
        List<Device> DeviceList = DeviceRepository.findDevicesByUserId(user_id);
        return DeviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public int insert(DeviceDetailsDTO DeviceDTO) {
        Device Device = DeviceBuilder.toEntity(DeviceDTO);
        Device = DeviceRepository.save(Device);
        LOGGER.debug("Device with id {} was inserted in db", Device.getId());
        return Device.getId();
    }

    public void deleteDevice(int id) {
        LOGGER.info("Deleting Device with ID: {}", id);

        // Verifica ca Device-ul exista
        if (!DeviceRepository.existsById(id)) {
            throw new RuntimeException("Device not found with id: " + id);
        }

        // Șterge Device-ul
        DeviceRepository.deleteById(id);

        LOGGER.info("Device deleted successfully: {}", id);
    }

    public void updateDevice(Integer id, DeviceDetailsDTO dto) {
        LOGGER.info("Updating Device with ID: {}", id);

        // Gasește Device-ul
        Device Device = DeviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));

        // Actualizeaza campurile
        Device.setName(dto.getName());
        Device.setMCV(dto.getMCV());
        Device.setUserId(dto.getUserId());

        // Salveaza
        DeviceRepository.save(Device);

        LOGGER.info("Device updated successfully: {}", id);
    }

}
