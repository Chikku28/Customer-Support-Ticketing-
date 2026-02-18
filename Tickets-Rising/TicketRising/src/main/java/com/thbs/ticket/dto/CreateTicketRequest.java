package com.thbs.ticket.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
    @NotNull Long customerId,              
    @NotBlank @Size(max = 120) String subject,
    @NotBlank @Size(max = 2000) String description
) {}

