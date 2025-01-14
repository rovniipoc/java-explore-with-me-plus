package ru.practicum.ewm;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitInputDto endpointHitInputDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setIp(endpointHitInputDto.getIp());
        endpointHit.setApp(endpointHitInputDto.getApp());
        endpointHit.setUri(endpointHitInputDto.getUri());
        endpointHit.setTimestamp(endpointHitInputDto.getTimestamp());

        return endpointHit;
    }
}
