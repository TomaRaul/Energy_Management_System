package com.ds.ems.dtos.builders;

import com.ds.ems.dtos.DeviceDTO;
import com.ds.ems.dtos.DeviceDetailsDTO;
import com.ds.ems.entities.Device;


public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device Device) {
        return new DeviceDTO(Device.getId(), Device.getName(), Device.getMCV(), Device.getUserId());
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device Device) {
        return new DeviceDetailsDTO(Device.getId(), Device.getName(), Device.getMCV(), Device.getUserId());
    }

    public static Device toEntity(DeviceDetailsDTO DeviceDetailsDTO) {
        return new Device(DeviceDetailsDTO.getId(),
                DeviceDetailsDTO.getName(),
                DeviceDetailsDTO.getMCV(),
                DeviceDetailsDTO.getUserId());
    }
}
