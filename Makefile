PRODUCT_SERVICE_DIR := product-service
PRODUCT_SERVICE_MVN := ./mvnw
ORDER_SERVICE_DIR := order-service
ORDER_SERVICE_MVN := ./mvnw
ROOT_MVN := ./order-service/mvnw
ROOT_POM := -f pom.xml

.PHONY: help build-all product-build product-api product-test product-verify product-package product-clean order-build order-api order-test order-verify order-package order-clean verify-all docker-package docker-up docker-down docker-logs

help:
	@printf "Available targets:\n"
	@printf "  build-all       Build all Maven submodules from the repository root\n"
	@printf "  product-build   Build the product-service module\n"
	@printf "  product-api     Launch the product-service API\n"
	@printf "  product-test    Run product-service tests\n"
	@printf "  product-verify  Run product-service unit tests, integration tests, and coverage\n"
	@printf "  product-package Package the product-service application\n"
	@printf "  product-clean   Clean product-service build outputs\n"
	@printf "  order-build   Build the order-service module\n"
	@printf "  order-api     Launch the order-service API\n"
	@printf "  order-test    Run order-service tests\n"
	@printf "  order-verify  Run order-service unit tests, integration tests, and coverage\n"
	@printf "  order-package Package the order-service application\n"
	@printf "  order-clean   Clean order-service build outputs\n"
	@printf "  verify-all     Run verify for both services\n"
	@printf "  docker-package Build both service JARs for Docker\n"
	@printf "  docker-up      Build JARs and start the Docker Compose stack\n"
	@printf "  docker-down    Stop the Docker Compose stack\n"
	@printf "  docker-logs    Follow Docker Compose logs\n"

build-all:
	$(ROOT_MVN) $(ROOT_POM) clean compile

product-build:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) clean compile

product-api:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) spring-boot:run

product-test:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) test

product-verify:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) verify

product-package:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) clean package

product-clean:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) clean

order-build:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) clean compile

order-api:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) spring-boot:run

order-test:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) test

order-verify:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) verify

order-package:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) clean package

order-clean:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) clean

verify-all: product-verify order-verify

docker-package: product-package order-package

docker-up: docker-package
	docker compose up --build -d

docker-down:
	docker compose down

docker-logs:
	docker compose logs -f
