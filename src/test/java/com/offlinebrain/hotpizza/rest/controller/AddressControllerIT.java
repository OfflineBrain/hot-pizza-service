package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.AbstractIT;
import com.offlinebrain.hotpizza.data.model.Address;
import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.data.repository.AddressRepository;
import com.offlinebrain.hotpizza.data.repository.ClientRepository;
import com.offlinebrain.hotpizza.rest.model.address.AddressModel;
import com.offlinebrain.hotpizza.rest.model.address.CreateAddressDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AddressController Integration Test")
public class AddressControllerIT extends AbstractIT {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Required beans loaded")
    void contextLoads() {
        Assertions.assertNotNull(addressRepository);
        Assertions.assertNotNull(clientRepository);
        Assertions.assertNotNull(restTemplate);
    }

    @Nested
    @DisplayName("Address creation API")
    class AddressCreation {
        String path = "/addresses";
        String addressValue = "Some address to be saved";
        private ClientUser clientUser;

        @BeforeEach
        void setUp() {
            clientUser = clientRepository.saveAndFlush(ClientUser.builder()
                    .name("AddressControllerIT.User1")
                    .phone("0001122333")
                    .build());
        }

        @AfterEach
        void tearDown() {
            addressRepository.deleteAll();
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Create address")
        void testCreateAddress() {
            ResponseEntity<EntityModel<AddressModel>> response = restTemplate.exchange(path, HttpMethod.POST,
                    new HttpEntity<>(CreateAddressDTO.builder()
                            .address(addressValue)
                            .clientUser(clientUser.getUuid())
                            .build()),
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            AddressModel address = response.getBody().getContent();
            assertNotNull(address);
            assertEquals(addressValue, address.getAddress());
            assertNotNull(address.getUuid());
            assertEquals(clientUser.getUuid(), address.getClientUser());
        }

    }

    @Nested
    @DisplayName("Address retrieve API")
    class AddressRetrieval {
        String addressValue = "Some address to be saved";
        String secondAddressValue = "Some another address to be saved";
        private ClientUser firstClientUser;
        private ClientUser secondClientUser;

        @BeforeEach
        void setUp() {
            firstClientUser = clientRepository.saveAndFlush(ClientUser.builder()
                    .name("AddressControllerIT.User1")
                    .phone("0001122333")
                    .build());
            secondClientUser = clientRepository.saveAndFlush(ClientUser.builder()
                    .name("AddressControllerIT.User2")
                    .phone("0001122334")
                    .build());
        }

        @AfterEach
        void tearDown() {
            addressRepository.deleteAll();
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Get address by UUID")
        void testAddressRetrieve() {
            Address address = addressRepository.saveAndFlush(
                    Address.builder().address(addressValue).clientUser(firstClientUser).build());

            String path = "/addresses/" + address.getUuid().toString();

            ResponseEntity<EntityModel<AddressModel>> response = restTemplate.exchange(path, HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            AddressModel addressModel = response.getBody().getContent();
            assertNotNull(addressModel);
            assertEquals(addressValue, addressModel.getAddress());
            assertNotNull(addressModel.getUuid());
            assertEquals(firstClientUser.getUuid(), addressModel.getClientUser());
        }


        @Test
        @DisplayName("Get address by client UUID")
        void testAddressRetrieveByClient() {
            List<Address> addresses = new ArrayList<>();
            addresses.add(addressRepository.saveAndFlush(
                    Address.builder().address(addressValue).clientUser(secondClientUser).build()));
            addresses.add(addressRepository.saveAndFlush(
                    Address.builder().address(secondAddressValue).clientUser(secondClientUser).build()));

            String path = "/clients/" + secondClientUser.getUuid().toString() + "/addresses";

            ResponseEntity<CollectionModel<EntityModel<AddressModel>>> response = restTemplate.exchange(path,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            Collection<EntityModel<AddressModel>> collection = response.getBody().getContent();
            assertFalse(collection.isEmpty());
            List<AddressModel> categories = collection.stream().map(EntityModel::getContent).toList();
            assertEquals(addresses.size(), categories.size());
            assertTrue(categories.stream()
                    .allMatch(categoryDTO -> categoryDTO.getClientUser().equals(secondClientUser.getUuid())));
        }
    }

    @Nested
    @DisplayName("Address delete API")
    class AddressDeletion {

        String addressValue = "Some address to be saved";
        private ClientUser clientUser;

        @BeforeEach
        void setUp() {
            clientUser = clientRepository.saveAndFlush(ClientUser.builder()
                    .name("AddressControllerIT.User1")
                    .phone("0001122333")
                    .build());
        }

        @AfterEach
        void tearDown() {
            addressRepository.deleteAll();
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Delete address")
        void testDeleteByUUID() {
            Address address = addressRepository.saveAndFlush(
                    Address.builder().address(addressValue).clientUser(clientUser).build());

            String path = "/addresses/" + address.getUuid().toString();

            ResponseEntity<Object> response = restTemplate.exchange(path,
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(addressRepository.findById(address.getUuid()).isEmpty());
        }
    }
}
