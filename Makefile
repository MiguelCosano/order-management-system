PRODUCT_SERVICE_DIR := product-service
PRODUCT_SERVICE_MVN := ./mvnw
ORDER_SERVICE_DIR := order-service
ORDER_SERVICE_MVN := ./mvnw

.PHONY: help product-build product-api product-test product-package product-clean order-build order-api order-test order-package order-clean

help:
	@printf "Available targets:\n"
	@printf "  product-build   Build the product-service module\n"
	@printf "  product-api     Launch the product-service API\n"
	@printf "  product-test    Run product-service tests\n"
	@printf "  product-package Package the product-service application\n"
	@printf "  product-clean   Clean product-service build outputs\n"
	@printf "  order-build   Build the order-service module\n"
	@printf "  order-api     Launch the order-service API\n"
	@printf "  order-test    Run order-service tests\n"
	@printf "  order-package Package the order-service application\n"
	@printf "  order-clean   Clean order-service build outputs\n"

product-build:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) clean compile

product-api:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) spring-boot:run

product-test:
	cd $(PRODUCT_SERVICE_DIR) && $(PRODUCT_SERVICE_MVN) test

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

order-package:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) clean package

order-clean:
	cd $(ORDER_SERVICE_DIR) && $(ORDER_SERVICE_MVN) clean
