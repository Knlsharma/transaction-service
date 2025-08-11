#!/bin/bash

# Configuration
TEST_CLASSES=(
    "com.transaction_service.app.integration_and_unit.TransactionTests:TransactionTests"
    "com.transaction_service.app.integration_and_unit.AccountTests:AccountTests"
)

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Functions
show_menu() {
    echo -e "${BLUE}🧪 Spring Boot Test Runner${NC}"
    echo "=========================="
    echo "1. Run all tests"
    echo "2. Run TransactionTests only"
    echo "3. Run AccountTests only"
    echo "4. Run tests with detailed output"
    echo "5. Run tests and generate report"
    echo "6. Exit"
    echo ""
    read -p "Choose an option (1-6): " choice
}

run_single_test() {
    local test_class=$1
    local test_name=$2
    local verbose=${3:-false}

    echo -e "\n${YELLOW}🚀 Running $test_name...${NC}"

    if [ "$verbose" = true ]; then
        mvn test -Dtest=$test_class -X
    else
        mvn test -Dtest=$test_class
    fi

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $test_name completed successfully${NC}"
        return 0
    else
        echo -e "${RED}❌ $test_name failed${NC}"
        return 1
    fi
}

run_all_tests() {
    local verbose=${1:-false}
    local passed=0
    local failed=0

    echo -e "${YELLOW}🏁 Running all test classes...${NC}"

    for test_entry in "${TEST_CLASSES[@]}"; do
        IFS=':' read -r test_class test_name <<< "$test_entry"

        if run_single_test "$test_class" "$test_name" "$verbose"; then
            ((passed++))
        else
            ((failed++))
        fi
        echo ""
    done

    # Summary
    echo "=================================="
    echo -e "${BLUE}📊 Final Summary${NC}"
    echo "=================================="
    echo -e "${GREEN}✅ Passed: $passed${NC}"
    echo -e "${RED}❌ Failed: $failed${NC}"
    echo -e "Total: $((passed + failed))"

    return $failed
}

# Main script
case ${1:-menu} in
    "all")
        run_all_tests
        ;;
    "transaction")
        run_single_test "com.transaction_service.app.integration_and_unit.TransactionTests" "TransactionTests"
        ;;
    "account")
        run_single_test "com.transaction_service.app.integration_and_unit.AccountTests" "AccountTests"
        ;;
    "verbose")
        run_all_tests true
        ;;
    "menu"|*)
        while true; do
            show_menu
            case $choice in
                1)
                    run_all_tests
                    ;;
                2)
                    run_single_test "com.transaction_service.app.integration_and_unit.TransactionTests" "TransactionTests"
                    ;;
                3)
                    run_single_test "com.transaction_service.app.integration_and_unit.AccountTests" "AccountTests"
                    ;;
                4)
                    run_all_tests true
                    ;;
                5)
                    echo "Generating test report..."
                    mvn test surefire-report:report
                    echo "Report generated in target/site/surefire-report.html"
                    ;;
                6)
                    echo "Goodbye!"
                    exit 0
                    ;;
                *)
                    echo "Invalid option. Please try again."
                    ;;
            esac
            echo ""
            read -p "Press Enter to continue..."
            clear
        done
        ;;
esac
