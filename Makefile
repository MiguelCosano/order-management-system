PRODUCT_SERVICE_DIR := product-service
PRODUCT_SERVICE_MVN := ./mvnw
ORDER_SERVICE_DIR := order-service
ORDER_SERVICE_MVN := ./mvnw
GATEWAY_DIR := api-gateway
ROOT_MVN := ./order-service/mvnw
ROOT_POM := -f pom.xml
GATEWAY_POM := -f $(GATEWAY_DIR)/pom.xml

.PHONY: help build-all product-build product-api product-test product-verify product-package product-clean order-build order-api order-test order-verify order-package order-clean gateway-build gateway-api gateway-test gateway-verify gateway-package gateway-clean verify-all docker-package docker-up docker-down docker-logs

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
	@printf "  gateway-build Build the api-gateway module\n"
	@printf "  gateway-api   Launch the api-gateway API\n"
	@printf "  gateway-test  Run api-gateway tests\n"
	@printf "  gateway-verify Run api-gateway tests and verification tasks\n"
	@printf "  gateway-package Package the api-gateway application\n"
	@printf "  gateway-clean Clean api-gateway build outputs\n"
	@printf "  verify-all     Run verify for all services\n"
	@printf "  docker-package Build all service JARs for Docker\n"
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

gateway-build:
	$(ROOT_MVN) $(GATEWAY_POM) clean compile

gateway-api:
	$(ROOT_MVN) $(GATEWAY_POM) spring-boot:run

gateway-test:
	$(ROOT_MVN) $(GATEWAY_POM) test

gateway-verify:
	$(ROOT_MVN) $(GATEWAY_POM) verify

gateway-package:
	$(ROOT_MVN) $(GATEWAY_POM) clean package

gateway-clean:
	$(ROOT_MVN) $(GATEWAY_POM) clean

verify-all: product-verify order-verify gateway-verify

docker-package: product-package order-package gateway-package

docker-up: docker-package
	docker compose up --build -d

docker-down:
	docker compose down

docker-logs:
	docker compose logs -f
