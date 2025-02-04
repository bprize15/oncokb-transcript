package org.mskcc.oncokb.curation.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mskcc.oncokb.curation.IntegrationTest;
import org.mskcc.oncokb.curation.domain.CompanionDiagnosticDevice;
import org.mskcc.oncokb.curation.domain.FdaSubmission;
import org.mskcc.oncokb.curation.domain.SpecimenType;
import org.mskcc.oncokb.curation.repository.CompanionDiagnosticDeviceRepository;
import org.mskcc.oncokb.curation.service.CompanionDiagnosticDeviceService;
import org.mskcc.oncokb.curation.service.criteria.CompanionDiagnosticDeviceCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CompanionDiagnosticDeviceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CompanionDiagnosticDeviceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBBBBBBB";

    private static final String DEFAULT_INDICATION_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_INDICATION_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_PLATFORM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_PLATFORM_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/companion-diagnostic-devices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompanionDiagnosticDeviceRepository companionDiagnosticDeviceRepository;

    @Mock
    private CompanionDiagnosticDeviceRepository companionDiagnosticDeviceRepositoryMock;

    @Mock
    private CompanionDiagnosticDeviceService companionDiagnosticDeviceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompanionDiagnosticDeviceMockMvc;

    private CompanionDiagnosticDevice companionDiagnosticDevice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanionDiagnosticDevice createEntity(EntityManager em) {
        CompanionDiagnosticDevice companionDiagnosticDevice = new CompanionDiagnosticDevice()
            .name(DEFAULT_NAME)
            .manufacturer(DEFAULT_MANUFACTURER)
            .indicationDetails(DEFAULT_INDICATION_DETAILS)
            .platformType(DEFAULT_PLATFORM_TYPE)
            .lastUpdated(DEFAULT_LAST_UPDATED);
        return companionDiagnosticDevice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanionDiagnosticDevice createUpdatedEntity(EntityManager em) {
        CompanionDiagnosticDevice companionDiagnosticDevice = new CompanionDiagnosticDevice()
            .name(UPDATED_NAME)
            .manufacturer(UPDATED_MANUFACTURER)
            .indicationDetails(UPDATED_INDICATION_DETAILS)
            .platformType(UPDATED_PLATFORM_TYPE)
            .lastUpdated(UPDATED_LAST_UPDATED);
        return companionDiagnosticDevice;
    }

    @BeforeEach
    public void initTest() {
        companionDiagnosticDevice = createEntity(em);
    }

    @Test
    @Transactional
    void createCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeCreate = companionDiagnosticDeviceRepository.findAll().size();
        // Create the CompanionDiagnosticDevice
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isCreated());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeCreate + 1);
        CompanionDiagnosticDevice testCompanionDiagnosticDevice = companionDiagnosticDeviceList.get(
            companionDiagnosticDeviceList.size() - 1
        );
        assertThat(testCompanionDiagnosticDevice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompanionDiagnosticDevice.getManufacturer()).isEqualTo(DEFAULT_MANUFACTURER);
        assertThat(testCompanionDiagnosticDevice.getIndicationDetails()).isEqualTo(DEFAULT_INDICATION_DETAILS);
        assertThat(testCompanionDiagnosticDevice.getPlatformType()).isEqualTo(DEFAULT_PLATFORM_TYPE);
        assertThat(testCompanionDiagnosticDevice.getLastUpdated()).isEqualTo(DEFAULT_LAST_UPDATED);
    }

    @Test
    @Transactional
    void createCompanionDiagnosticDeviceWithExistingId() throws Exception {
        // Create the CompanionDiagnosticDevice with an existing ID
        companionDiagnosticDevice.setId(1L);

        int databaseSizeBeforeCreate = companionDiagnosticDeviceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = companionDiagnosticDeviceRepository.findAll().size();
        // set the field null
        companionDiagnosticDevice.setName(null);

        // Create the CompanionDiagnosticDevice, which fails.

        restCompanionDiagnosticDeviceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkManufacturerIsRequired() throws Exception {
        int databaseSizeBeforeTest = companionDiagnosticDeviceRepository.findAll().size();
        // set the field null
        companionDiagnosticDevice.setManufacturer(null);

        // Create the CompanionDiagnosticDevice, which fails.

        restCompanionDiagnosticDeviceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevices() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList
        restCompanionDiagnosticDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companionDiagnosticDevice.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER)))
            .andExpect(jsonPath("$.[*].indicationDetails").value(hasItem(DEFAULT_INDICATION_DETAILS)))
            .andExpect(jsonPath("$.[*].platformType").value(hasItem(DEFAULT_PLATFORM_TYPE)))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())));
    }

    @Test
    @Transactional
    void getCompanionDiagnosticDevice() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get the companionDiagnosticDevice
        restCompanionDiagnosticDeviceMockMvc
            .perform(get(ENTITY_API_URL_ID, companionDiagnosticDevice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(companionDiagnosticDevice.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.manufacturer").value(DEFAULT_MANUFACTURER))
            .andExpect(jsonPath("$.indicationDetails").value(DEFAULT_INDICATION_DETAILS))
            .andExpect(jsonPath("$.platformType").value(DEFAULT_PLATFORM_TYPE))
            .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()));
    }

    @Test
    @Transactional
    void getCompanionDiagnosticDevicesByIdFiltering() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        Long id = companionDiagnosticDevice.getId();

        defaultCompanionDiagnosticDeviceShouldBeFound("id.equals=" + id);
        defaultCompanionDiagnosticDeviceShouldNotBeFound("id.notEquals=" + id);

        defaultCompanionDiagnosticDeviceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCompanionDiagnosticDeviceShouldNotBeFound("id.greaterThan=" + id);

        defaultCompanionDiagnosticDeviceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCompanionDiagnosticDeviceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where name equals to DEFAULT_NAME
        defaultCompanionDiagnosticDeviceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the companionDiagnosticDeviceList where name equals to UPDATED_NAME
        defaultCompanionDiagnosticDeviceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where name not equals to DEFAULT_NAME
        defaultCompanionDiagnosticDeviceShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the companionDiagnosticDeviceList where name not equals to UPDATED_NAME
        defaultCompanionDiagnosticDeviceShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCompanionDiagnosticDeviceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the companionDiagnosticDeviceList where name equals to UPDATED_NAME
        defaultCompanionDiagnosticDeviceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where name is not null
        defaultCompanionDiagnosticDeviceShouldBeFound("name.specified=true");

        // Get all the companionDiagnosticDeviceList where name is null
        defaultCompanionDiagnosticDeviceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByNameContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where name contains DEFAULT_NAME
        defaultCompanionDiagnosticDeviceShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the companionDiagnosticDeviceList where name contains UPDATED_NAME
        defaultCompanionDiagnosticDeviceShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where name does not contain DEFAULT_NAME
        defaultCompanionDiagnosticDeviceShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the companionDiagnosticDeviceList where name does not contain UPDATED_NAME
        defaultCompanionDiagnosticDeviceShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByManufacturerIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where manufacturer equals to DEFAULT_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldBeFound("manufacturer.equals=" + DEFAULT_MANUFACTURER);

        // Get all the companionDiagnosticDeviceList where manufacturer equals to UPDATED_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldNotBeFound("manufacturer.equals=" + UPDATED_MANUFACTURER);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByManufacturerIsNotEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where manufacturer not equals to DEFAULT_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldNotBeFound("manufacturer.notEquals=" + DEFAULT_MANUFACTURER);

        // Get all the companionDiagnosticDeviceList where manufacturer not equals to UPDATED_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldBeFound("manufacturer.notEquals=" + UPDATED_MANUFACTURER);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByManufacturerIsInShouldWork() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where manufacturer in DEFAULT_MANUFACTURER or UPDATED_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldBeFound("manufacturer.in=" + DEFAULT_MANUFACTURER + "," + UPDATED_MANUFACTURER);

        // Get all the companionDiagnosticDeviceList where manufacturer equals to UPDATED_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldNotBeFound("manufacturer.in=" + UPDATED_MANUFACTURER);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByManufacturerIsNullOrNotNull() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where manufacturer is not null
        defaultCompanionDiagnosticDeviceShouldBeFound("manufacturer.specified=true");

        // Get all the companionDiagnosticDeviceList where manufacturer is null
        defaultCompanionDiagnosticDeviceShouldNotBeFound("manufacturer.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByManufacturerContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where manufacturer contains DEFAULT_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldBeFound("manufacturer.contains=" + DEFAULT_MANUFACTURER);

        // Get all the companionDiagnosticDeviceList where manufacturer contains UPDATED_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldNotBeFound("manufacturer.contains=" + UPDATED_MANUFACTURER);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByManufacturerNotContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where manufacturer does not contain DEFAULT_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldNotBeFound("manufacturer.doesNotContain=" + DEFAULT_MANUFACTURER);

        // Get all the companionDiagnosticDeviceList where manufacturer does not contain UPDATED_MANUFACTURER
        defaultCompanionDiagnosticDeviceShouldBeFound("manufacturer.doesNotContain=" + UPDATED_MANUFACTURER);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByIndicationDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where indicationDetails equals to DEFAULT_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldBeFound("indicationDetails.equals=" + DEFAULT_INDICATION_DETAILS);

        // Get all the companionDiagnosticDeviceList where indicationDetails equals to UPDATED_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldNotBeFound("indicationDetails.equals=" + UPDATED_INDICATION_DETAILS);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByIndicationDetailsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where indicationDetails not equals to DEFAULT_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldNotBeFound("indicationDetails.notEquals=" + DEFAULT_INDICATION_DETAILS);

        // Get all the companionDiagnosticDeviceList where indicationDetails not equals to UPDATED_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldBeFound("indicationDetails.notEquals=" + UPDATED_INDICATION_DETAILS);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByIndicationDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where indicationDetails in DEFAULT_INDICATION_DETAILS or UPDATED_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldBeFound(
            "indicationDetails.in=" + DEFAULT_INDICATION_DETAILS + "," + UPDATED_INDICATION_DETAILS
        );

        // Get all the companionDiagnosticDeviceList where indicationDetails equals to UPDATED_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldNotBeFound("indicationDetails.in=" + UPDATED_INDICATION_DETAILS);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByIndicationDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where indicationDetails is not null
        defaultCompanionDiagnosticDeviceShouldBeFound("indicationDetails.specified=true");

        // Get all the companionDiagnosticDeviceList where indicationDetails is null
        defaultCompanionDiagnosticDeviceShouldNotBeFound("indicationDetails.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByIndicationDetailsContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where indicationDetails contains DEFAULT_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldBeFound("indicationDetails.contains=" + DEFAULT_INDICATION_DETAILS);

        // Get all the companionDiagnosticDeviceList where indicationDetails contains UPDATED_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldNotBeFound("indicationDetails.contains=" + UPDATED_INDICATION_DETAILS);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByIndicationDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where indicationDetails does not contain DEFAULT_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldNotBeFound("indicationDetails.doesNotContain=" + DEFAULT_INDICATION_DETAILS);

        // Get all the companionDiagnosticDeviceList where indicationDetails does not contain UPDATED_INDICATION_DETAILS
        defaultCompanionDiagnosticDeviceShouldBeFound("indicationDetails.doesNotContain=" + UPDATED_INDICATION_DETAILS);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByPlatformTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where platformType equals to DEFAULT_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldBeFound("platformType.equals=" + DEFAULT_PLATFORM_TYPE);

        // Get all the companionDiagnosticDeviceList where platformType equals to UPDATED_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldNotBeFound("platformType.equals=" + UPDATED_PLATFORM_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByPlatformTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where platformType not equals to DEFAULT_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldNotBeFound("platformType.notEquals=" + DEFAULT_PLATFORM_TYPE);

        // Get all the companionDiagnosticDeviceList where platformType not equals to UPDATED_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldBeFound("platformType.notEquals=" + UPDATED_PLATFORM_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByPlatformTypeIsInShouldWork() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where platformType in DEFAULT_PLATFORM_TYPE or UPDATED_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldBeFound("platformType.in=" + DEFAULT_PLATFORM_TYPE + "," + UPDATED_PLATFORM_TYPE);

        // Get all the companionDiagnosticDeviceList where platformType equals to UPDATED_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldNotBeFound("platformType.in=" + UPDATED_PLATFORM_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByPlatformTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where platformType is not null
        defaultCompanionDiagnosticDeviceShouldBeFound("platformType.specified=true");

        // Get all the companionDiagnosticDeviceList where platformType is null
        defaultCompanionDiagnosticDeviceShouldNotBeFound("platformType.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByPlatformTypeContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where platformType contains DEFAULT_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldBeFound("platformType.contains=" + DEFAULT_PLATFORM_TYPE);

        // Get all the companionDiagnosticDeviceList where platformType contains UPDATED_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldNotBeFound("platformType.contains=" + UPDATED_PLATFORM_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByPlatformTypeNotContainsSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where platformType does not contain DEFAULT_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldNotBeFound("platformType.doesNotContain=" + DEFAULT_PLATFORM_TYPE);

        // Get all the companionDiagnosticDeviceList where platformType does not contain UPDATED_PLATFORM_TYPE
        defaultCompanionDiagnosticDeviceShouldBeFound("platformType.doesNotContain=" + UPDATED_PLATFORM_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByLastUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where lastUpdated equals to DEFAULT_LAST_UPDATED
        defaultCompanionDiagnosticDeviceShouldBeFound("lastUpdated.equals=" + DEFAULT_LAST_UPDATED);

        // Get all the companionDiagnosticDeviceList where lastUpdated equals to UPDATED_LAST_UPDATED
        defaultCompanionDiagnosticDeviceShouldNotBeFound("lastUpdated.equals=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByLastUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where lastUpdated not equals to DEFAULT_LAST_UPDATED
        defaultCompanionDiagnosticDeviceShouldNotBeFound("lastUpdated.notEquals=" + DEFAULT_LAST_UPDATED);

        // Get all the companionDiagnosticDeviceList where lastUpdated not equals to UPDATED_LAST_UPDATED
        defaultCompanionDiagnosticDeviceShouldBeFound("lastUpdated.notEquals=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByLastUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where lastUpdated in DEFAULT_LAST_UPDATED or UPDATED_LAST_UPDATED
        defaultCompanionDiagnosticDeviceShouldBeFound("lastUpdated.in=" + DEFAULT_LAST_UPDATED + "," + UPDATED_LAST_UPDATED);

        // Get all the companionDiagnosticDeviceList where lastUpdated equals to UPDATED_LAST_UPDATED
        defaultCompanionDiagnosticDeviceShouldNotBeFound("lastUpdated.in=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByLastUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        // Get all the companionDiagnosticDeviceList where lastUpdated is not null
        defaultCompanionDiagnosticDeviceShouldBeFound("lastUpdated.specified=true");

        // Get all the companionDiagnosticDeviceList where lastUpdated is null
        defaultCompanionDiagnosticDeviceShouldNotBeFound("lastUpdated.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesByFdaSubmissionIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);
        FdaSubmission fdaSubmission;
        if (TestUtil.findAll(em, FdaSubmission.class).isEmpty()) {
            fdaSubmission = FdaSubmissionResourceIT.createEntity(em);
            em.persist(fdaSubmission);
            em.flush();
        } else {
            fdaSubmission = TestUtil.findAll(em, FdaSubmission.class).get(0);
        }
        em.persist(fdaSubmission);
        em.flush();
        companionDiagnosticDevice.addFdaSubmission(fdaSubmission);
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);
        Long fdaSubmissionId = fdaSubmission.getId();

        // Get all the companionDiagnosticDeviceList where fdaSubmission equals to fdaSubmissionId
        defaultCompanionDiagnosticDeviceShouldBeFound("fdaSubmissionId.equals=" + fdaSubmissionId);

        // Get all the companionDiagnosticDeviceList where fdaSubmission equals to (fdaSubmissionId + 1)
        defaultCompanionDiagnosticDeviceShouldNotBeFound("fdaSubmissionId.equals=" + (fdaSubmissionId + 1));
    }

    @Test
    @Transactional
    void getAllCompanionDiagnosticDevicesBySpecimenTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);
        SpecimenType specimenType;
        if (TestUtil.findAll(em, SpecimenType.class).isEmpty()) {
            specimenType = SpecimenTypeResourceIT.createEntity(em);
            em.persist(specimenType);
            em.flush();
        } else {
            specimenType = TestUtil.findAll(em, SpecimenType.class).get(0);
        }
        em.persist(specimenType);
        em.flush();
        companionDiagnosticDevice.addSpecimenType(specimenType);
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);
        Long specimenTypeId = specimenType.getId();

        // Get all the companionDiagnosticDeviceList where specimenType equals to specimenTypeId
        defaultCompanionDiagnosticDeviceShouldBeFound("specimenTypeId.equals=" + specimenTypeId);

        // Get all the companionDiagnosticDeviceList where specimenType equals to (specimenTypeId + 1)
        defaultCompanionDiagnosticDeviceShouldNotBeFound("specimenTypeId.equals=" + (specimenTypeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompanionDiagnosticDeviceShouldBeFound(String filter) throws Exception {
        restCompanionDiagnosticDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companionDiagnosticDevice.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER)))
            .andExpect(jsonPath("$.[*].indicationDetails").value(hasItem(DEFAULT_INDICATION_DETAILS)))
            .andExpect(jsonPath("$.[*].platformType").value(hasItem(DEFAULT_PLATFORM_TYPE)))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())));

        // Check, that the count call also returns 1
        restCompanionDiagnosticDeviceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompanionDiagnosticDeviceShouldNotBeFound(String filter) throws Exception {
        restCompanionDiagnosticDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompanionDiagnosticDeviceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompanionDiagnosticDevice() throws Exception {
        // Get the companionDiagnosticDevice
        restCompanionDiagnosticDeviceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCompanionDiagnosticDevice() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();

        // Update the companionDiagnosticDevice
        CompanionDiagnosticDevice updatedCompanionDiagnosticDevice = companionDiagnosticDeviceRepository
            .findById(companionDiagnosticDevice.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCompanionDiagnosticDevice are not directly saved in db
        em.detach(updatedCompanionDiagnosticDevice);
        updatedCompanionDiagnosticDevice
            .name(UPDATED_NAME)
            .manufacturer(UPDATED_MANUFACTURER)
            .indicationDetails(UPDATED_INDICATION_DETAILS)
            .platformType(UPDATED_PLATFORM_TYPE)
            .lastUpdated(UPDATED_LAST_UPDATED);

        restCompanionDiagnosticDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCompanionDiagnosticDevice.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCompanionDiagnosticDevice))
            )
            .andExpect(status().isOk());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
        CompanionDiagnosticDevice testCompanionDiagnosticDevice = companionDiagnosticDeviceList.get(
            companionDiagnosticDeviceList.size() - 1
        );
        assertThat(testCompanionDiagnosticDevice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompanionDiagnosticDevice.getManufacturer()).isEqualTo(UPDATED_MANUFACTURER);
        assertThat(testCompanionDiagnosticDevice.getIndicationDetails()).isEqualTo(UPDATED_INDICATION_DETAILS);
        assertThat(testCompanionDiagnosticDevice.getPlatformType()).isEqualTo(UPDATED_PLATFORM_TYPE);
        assertThat(testCompanionDiagnosticDevice.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void putNonExistingCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();
        companionDiagnosticDevice.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companionDiagnosticDevice.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();
        companionDiagnosticDevice.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();
        companionDiagnosticDevice.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompanionDiagnosticDeviceWithPatch() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();

        // Update the companionDiagnosticDevice using partial update
        CompanionDiagnosticDevice partialUpdatedCompanionDiagnosticDevice = new CompanionDiagnosticDevice();
        partialUpdatedCompanionDiagnosticDevice.setId(companionDiagnosticDevice.getId());

        partialUpdatedCompanionDiagnosticDevice
            .manufacturer(UPDATED_MANUFACTURER)
            .indicationDetails(UPDATED_INDICATION_DETAILS)
            .lastUpdated(UPDATED_LAST_UPDATED);

        restCompanionDiagnosticDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanionDiagnosticDevice.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompanionDiagnosticDevice))
            )
            .andExpect(status().isOk());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
        CompanionDiagnosticDevice testCompanionDiagnosticDevice = companionDiagnosticDeviceList.get(
            companionDiagnosticDeviceList.size() - 1
        );
        assertThat(testCompanionDiagnosticDevice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompanionDiagnosticDevice.getManufacturer()).isEqualTo(UPDATED_MANUFACTURER);
        assertThat(testCompanionDiagnosticDevice.getIndicationDetails()).isEqualTo(UPDATED_INDICATION_DETAILS);
        assertThat(testCompanionDiagnosticDevice.getPlatformType()).isEqualTo(DEFAULT_PLATFORM_TYPE);
        assertThat(testCompanionDiagnosticDevice.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void fullUpdateCompanionDiagnosticDeviceWithPatch() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();

        // Update the companionDiagnosticDevice using partial update
        CompanionDiagnosticDevice partialUpdatedCompanionDiagnosticDevice = new CompanionDiagnosticDevice();
        partialUpdatedCompanionDiagnosticDevice.setId(companionDiagnosticDevice.getId());

        partialUpdatedCompanionDiagnosticDevice
            .name(UPDATED_NAME)
            .manufacturer(UPDATED_MANUFACTURER)
            .indicationDetails(UPDATED_INDICATION_DETAILS)
            .platformType(UPDATED_PLATFORM_TYPE)
            .lastUpdated(UPDATED_LAST_UPDATED);

        restCompanionDiagnosticDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanionDiagnosticDevice.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompanionDiagnosticDevice))
            )
            .andExpect(status().isOk());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
        CompanionDiagnosticDevice testCompanionDiagnosticDevice = companionDiagnosticDeviceList.get(
            companionDiagnosticDeviceList.size() - 1
        );
        assertThat(testCompanionDiagnosticDevice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompanionDiagnosticDevice.getManufacturer()).isEqualTo(UPDATED_MANUFACTURER);
        assertThat(testCompanionDiagnosticDevice.getIndicationDetails()).isEqualTo(UPDATED_INDICATION_DETAILS);
        assertThat(testCompanionDiagnosticDevice.getPlatformType()).isEqualTo(UPDATED_PLATFORM_TYPE);
        assertThat(testCompanionDiagnosticDevice.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void patchNonExistingCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();
        companionDiagnosticDevice.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, companionDiagnosticDevice.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();
        companionDiagnosticDevice.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompanionDiagnosticDevice() throws Exception {
        int databaseSizeBeforeUpdate = companionDiagnosticDeviceRepository.findAll().size();
        companionDiagnosticDevice.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanionDiagnosticDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(companionDiagnosticDevice))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanionDiagnosticDevice in the database
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompanionDiagnosticDevice() throws Exception {
        // Initialize the database
        companionDiagnosticDeviceRepository.saveAndFlush(companionDiagnosticDevice);

        int databaseSizeBeforeDelete = companionDiagnosticDeviceRepository.findAll().size();

        // Delete the companionDiagnosticDevice
        restCompanionDiagnosticDeviceMockMvc
            .perform(delete(ENTITY_API_URL_ID, companionDiagnosticDevice.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CompanionDiagnosticDevice> companionDiagnosticDeviceList = companionDiagnosticDeviceRepository.findAll();
        assertThat(companionDiagnosticDeviceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
