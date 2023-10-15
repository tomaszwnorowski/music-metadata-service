# :musical_note: Music Metadata Service (MMS) - recruitment task

Repository demonstrates a solution to a recruitment task. Please note that time invested into solution was capped
at 6-8h.

# :european_castle: Architecture
The following diagram visualizes long term architecture that should serve as a north star.

![Alt text](architecture.png?raw=true "Architecture")

## :office: Modularization
The solution focuses primarily on architecture that would be most optimal for a smooth transition from
modular monolith (optimized for experimentation) to microservices when scaling issues comes into play.
Please note that splitting solution into 3 domains (artists, tracks, recommendations) poses risk and
typically shouldn't be done without deeply understanding the domain.

![Alt text](modules.png?raw=true "Modules")

The benefits of using the modularized monolith approach include:
* :recycle: faster feedback loop by recompiling only the modules impacted by the introduced changes
* :construction: allows to better understand boundaries and access pattern of each module
* :anger: reduces the likelihood of dealing with a lot of merge conflicts
* :ant: better development experience when working with multiple engineers on the same project
* :mortar_board: benefiting from simplicity of having a single unit of deployment
* :checkered_flag: possibility of compiling, building and testing multiple modules at the same time

## :tv: JVM API
Most of the modules should be shipped in two parts. First part is the API module that will become compile time dependency
of any other module that would like to consume it. Second part is the implementation (e.g. embedded) that will become the
runtime dependency when the application is started. This separation achieves following benefits:
* :shower: clean separation between part of the module that is intended to be used by the client code and internal implementation
* :rocket: faster built time of the whole project when changes impact only the implementation of the module
* :electric_plug: allows to easily swap in/out different implementation (e.g. replace embedded implementation with a call to http service)

## :computer: REST API
Although it may be feasible to divide each module into three distinct components, namely JVM API, implementation,
and protocol (e.g. REST), instead of just API and implementation, this approach may result in an unnecessary burden for
small to medium-sized projects.
Cross-cutting concerns, such as security, tracing, and monitoring, benefit from looking  at the REST API as a whole.
Moreover, from an API client perspective, it's desirable to interact with an API that is consistent in terms of naming
conventions, versioning and error handling rather than be surprised that separate parts of the API behave differently.
Furthermore, by running tests of multiple modules in parallel, it is possible to encounter issues with starting servers
on the same port, sharing or excessively utilizing system resources.

## :floppy_disk: Infrastructure
There are multiple ways of achieving the separation of database access in the context of multiple modules. On one end of
the spectrum, we have the possibility of connecting to a physically separate database, which eliminates the risk of
accidental coupling (e.g., joins between tables owned by separate modules, transactions spanning across multiple modules).
On the other end of the spectrum, we have solutions more in the spirit of multiplexing, which boils down to
using separate logical databases or schemas. The latter also ensures accidental coupling on the database layer is less
likely, but it's not as strict as the first approach. Since setting up a separate physical database for each module
introduces a lot of overhead, the logical separation seems like a reasonable compromise. However, even this approach
faces plenty of challenges:
* :truck: schema migration and code generation (e.g. Jooq) tools need to support multiple schemas
* :collision: failure to migrate a subset of schemas leads to a lot of issues and needs to be handled with care
* :slot_machine: application needs to handle connecting to, and switching between multiple schemas (in the case of spring multiple schemas
means either using multiple data sources with separate connection pools or even a lower-level approach of setting the
schema on connections acquired from the pool)
* :ticket: application has to specify in context of which schema given part of the request should happen (by either transferring
this information explicitly in method signatures or implicitly storing it in thread local)
* :passport_control: in case of specifying multiple data sources as beans, spring requires the one of them is primary and referencing rest
of them requires qualifier
* :warning: in the case of specifying multiple data sources as beans, spring boot starters that depend on the data source will configure
themselves with just one of them (primary)
* :ocean: in the case of using multiple data sources a care needs to be taken not to overwhelm database with the number of connections

Considering that the primary goal of the separation is the ability to promote modules to become services, it seems like
a huge cost to pay for this flexibility. This leads to the third alternative of using a table (or database object in
general) naming convention (e.g., by adding a prefix to each table) that makes it clear to which domain they belong.
However, the policy of not joining tables belonging to multiple domains or avoiding transactions spanning across
multiple modules won't be easily enforceable. It might require using tools like ArchUnit to discover unwanted
dependencies during build time or SQL execution listeners that would be able to tell which tables are being accessed
and throw an exception if necessary. Moreover, schema migration scripts that are module specific will need to be
interleaved with each-other.

## :fireworks: Functional requirements
All requested functionality has been covered with automated test. However, due to lack of time interaction with the API
happens using identifiers. There should be an API Gateway / Read API that would let users interact with the API in more
pleasant way. In particular search API for artists and tracks would be nice.

### Adding new tracks
**Assumptions**

Track can be associated with only a single artist (avoided many-to-many relation between artists and tracks) which is not true
in real world.

**Constraints**

For any given artist the track name must be unique.

**Improvements**

Since artists and track are separate modules while adding the track we should check whether artists exists.

### Edit Artist Name
**Assumptions**

Artists name is by default also their alias. When they change name to something else to new name is added to the list
of aliases.

**Constraints**

Alias of the artist must be unique.

### Fetch Artist Tracks
**Assumptions**

Pagination is needed to avoid fetching all the existing tracks. Keyset pagination was used to provide more natural
experience and support "infinite scroll" on the clients.

**Constraints**

In a single fetch client can get up to 10 tracks.

**Improvements**

When adding search API the implementation can be extended to support traversing tracks in custom order (e.g. genre, artist, length, other metadata...)

### Artist of the Day
**Assumptions**

There is a single instance of the application running in the cluster for now. Multiple would still work but it would be
inefficient. Since identifier used to uniquely identify artists is time based, it's very easy to cycle through them from
earliest to the latest.

**Constraints**

Precomputing artists of the day for few days ahead when application starts.

**Improvements**

Put an upper bound on how many days ahead (or behind) need to be stored.

## :open_file_folder: Operational and maintenance aspects

**In scope**
- health check exposed based on database connectivity (spring boot actuator)
- rest api versioning
- database indexes to ensure reads are fast
- database migrations using flyway
- database uses identifiers that are more efficient to index and search through than UUID
- cache using in memory data structure for artist of the day
- static code analysis
- code formatting
- ensuring correct version of dependencies across modules
- building project modules in parallel, using cache and incremental compilation
- building docker image using multi layer images (with layout efficient for spring boot)
- when testing relying on fixtures
- various types of tests (unit, integration, smoke) executed from fastest (unit) to slowest (integration)

**Out of scope**
- security (adding and modifying artists requires higher privilege)
- performance metrics (micrometer, prometheus, grafana)
- validation checks (more checks to ensure user input is checked)
- tracing (making sure that requests are easily trackable)
- logging (more custom logging)
- only scratching the surface when it comes to rest api testing (added single test)
