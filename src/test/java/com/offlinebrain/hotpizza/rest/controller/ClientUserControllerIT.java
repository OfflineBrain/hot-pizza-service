package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.AbstractIT;
import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.data.repository.ClientRepository;
import com.offlinebrain.hotpizza.rest.model.client.ClientModel;
import com.offlinebrain.hotpizza.rest.model.client.CreateClientDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ClientUserController Integration Test")
public class ClientUserControllerIT extends AbstractIT {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Required beans loaded")
    void contextLoads() {
        Assertions.assertNotNull(clientRepository);
        Assertions.assertNotNull(restTemplate);
    }

    @Nested
    @DisplayName("Client User creation API")
    class ClientUserCreation {
        @AfterEach
        void tearDown() {
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Create client")
        void testClientCreation() {
            String clientName = "ClientUserControllerIT.name";
            String clientPhone = "+0003322111";
            ResponseEntity<EntityModel<ClientModel>> response = restTemplate.exchange("/clients", HttpMethod.POST,
                    new HttpEntity<>(CreateClientDTO.builder()
                            .name(clientName)
                            .phone(clientPhone)
                            .build()),
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            ClientModel client = response.getBody().getContent();
            assertNotNull(client);
            assertEquals(clientName, client.getName());
            assertEquals(clientPhone, client.getPhone());
            assertNotNull(client.getUuid());
        }
    }

    @Nested
    @DisplayName("Client User retrieve API")
    class ClientUserRetrieval {

        private ClientUser clientUser;

        @BeforeEach
        void setUp() {
            clientUser = clientRepository.saveAndFlush(ClientUser.builder()
                    .phone("+3131313131313")
                    .name("ClientUserControllerIT.name").build());
        }

        @AfterEach
        void tearDown() {
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Get client by UUID")
        void testGetByUUID() {
            String path = "/clients/" + clientUser.getUuid();
            ResponseEntity<EntityModel<ClientModel>> response = restTemplate.exchange(path, HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertClient(response);
        }

        @Test
        @DisplayName("Get client by phone number")
        void testGetByPhone() {
            String path = "/clients/phone/" + clientUser.getPhone();
            ResponseEntity<EntityModel<ClientModel>> response = restTemplate.exchange(path, HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertClient(response);
        }

        private void assertClient(ResponseEntity<EntityModel<ClientModel>> response) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            ClientModel client = response.getBody().getContent();
            assertNotNull(client);
            assertEquals(clientUser.getName(), client.getName());
            assertEquals(clientUser.getPhone(), client.getPhone());
            assertEquals(clientUser.getUuid(), client.getUuid());
        }
    }

    @Nested
    @DisplayName("Client User delete API")
    class ClientUserDeletion {
        private ClientUser clientUser;

        @BeforeEach
        void setUp() {
            clientUser = clientRepository.saveAndFlush(ClientUser.builder()
                    .phone("+3131313131313")
                    .name("ClientUserControllerIT.name").build());
        }

        @AfterEach
        void tearDown() {
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Delete client")
        void testDeleteByUUID() {
            String path = "/clients/" + clientUser.getUuid();
            ResponseEntity<EntityModel<ClientModel>> response = restTemplate.exchange(path, HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(clientRepository.findById(clientUser.getUuid()).isEmpty());
        }
    }
}
