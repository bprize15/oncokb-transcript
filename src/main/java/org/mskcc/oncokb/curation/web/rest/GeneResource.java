package org.mskcc.oncokb.curation.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mskcc.oncokb.curation.domain.Gene;
import org.mskcc.oncokb.curation.repository.GeneRepository;
import org.mskcc.oncokb.curation.service.GeneQueryService;
import org.mskcc.oncokb.curation.service.GeneService;
import org.mskcc.oncokb.curation.service.criteria.GeneCriteria;
import org.mskcc.oncokb.curation.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.mskcc.oncokb.curation.domain.Gene}.
 */
@RestController
@RequestMapping("/api")
public class GeneResource {

    private final Logger log = LoggerFactory.getLogger(GeneResource.class);

    private static final String ENTITY_NAME = "gene";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GeneService geneService;

    private final GeneRepository geneRepository;

    private final GeneQueryService geneQueryService;

    public GeneResource(GeneService geneService, GeneRepository geneRepository, GeneQueryService geneQueryService) {
        this.geneService = geneService;
        this.geneRepository = geneRepository;
        this.geneQueryService = geneQueryService;
    }

    /**
     * {@code POST  /genes} : Create a new gene.
     *
     * @param gene the gene to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gene, or with status {@code 400 (Bad Request)} if the gene has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/genes")
    public ResponseEntity<Gene> createGene(@Valid @RequestBody Gene gene) throws URISyntaxException {
        log.debug("REST request to save Gene : {}", gene);
        if (gene.getId() != null) {
            throw new BadRequestAlertException("A new gene cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Gene result = geneService.save(gene);
        return ResponseEntity.created(new URI("/api/genes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /genes/:id} : Updates an existing gene.
     *
     * @param id the id of the gene to save.
     * @param gene the gene to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gene,
     * or with status {@code 400 (Bad Request)} if the gene is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gene couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/genes/{id}")
    public ResponseEntity<Gene> updateGene(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Gene gene)
        throws URISyntaxException {
        log.debug("REST request to update Gene : {}, {}", id, gene);
        if (gene.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gene.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!geneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Gene result = geneService.save(gene);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gene.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /genes/:id} : Partial updates given fields of an existing gene, field will ignore if it is null
     *
     * @param id the id of the gene to save.
     * @param gene the gene to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gene,
     * or with status {@code 400 (Bad Request)} if the gene is not valid,
     * or with status {@code 404 (Not Found)} if the gene is not found,
     * or with status {@code 500 (Internal Server Error)} if the gene couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/genes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Gene> partialUpdateGene(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Gene gene
    ) throws URISyntaxException {
        log.debug("REST request to partial update Gene partially : {}, {}", id, gene);
        if (gene.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gene.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!geneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Gene> result = geneService.partialUpdate(gene);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gene.getId().toString())
        );
    }

    /**
     * {@code GET  /genes} : get all the genes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of genes in body.
     */
    @GetMapping("/genes")
    public ResponseEntity<List<Gene>> getAllGenes(GeneCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Genes by criteria: {}", criteria);
        Page<Gene> page = geneQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok()
            .headers(headers)
            .body(
                geneService.findAllByIdInWithGeneAliasAndEnsemblGenes(
                    page.getContent().stream().map(Gene::getId).collect(Collectors.toList())
                )
            );
    }

    /**
     * {@code GET  /genes/count} : count all the genes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/genes/count")
    public ResponseEntity<Long> countGenes(GeneCriteria criteria) {
        log.debug("REST request to count Genes by criteria: {}", criteria);
        return ResponseEntity.ok().body(geneQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /genes/:id} : get the "id" gene.
     *
     * @param id the id of the gene to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gene, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/genes/{id}")
    public ResponseEntity<Gene> getGene(@PathVariable Long id) {
        log.debug("REST request to get Gene : {}", id);
        Optional<Gene> gene = geneService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gene);
    }

    /**
     * {@code DELETE  /genes/:id} : delete the "id" gene.
     *
     * @param id the id of the gene to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/genes/{id}")
    public ResponseEntity<Void> deleteGene(@PathVariable Long id) {
        log.debug("REST request to delete Gene : {}", id);
        geneService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /genes/search?query=:query} : search for the gene corresponding
     * to the query.
     *
     * @param query the query of the gene search.
     * @return the result of the search.
     */
    @GetMapping("/genes/search")
    public ResponseEntity<List<Gene>> searchGenes(
        @RequestParam String query,
        @RequestParam(required = false) Boolean exact,
        Pageable pageable
    ) {
        log.debug("REST request to search for a page of Genes for query {}", query);
        if (exact == null) {
            exact = false;
        }
        Page<Gene> page = geneQueryService.findBySearchQuery(query, exact, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok()
            .headers(headers)
            .body(
                geneService.findAllByIdInWithGeneAliasAndEnsemblGenes(
                    page.getContent().stream().map(Gene::getId).collect(Collectors.toList())
                )
            );
    }
}
