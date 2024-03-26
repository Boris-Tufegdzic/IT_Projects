bold=$(tput bold)
normal=$(tput sgr0)

echo "${bold}These tests SHOULD NOT fail: (if you don't see any, it's good)${normal}"

# for script in ./src/test/script/added/*.sh; do  # $(find ./src/test/script/added/ -type f -print)
#     test_exec=$(bash "$script")
#     if [ "$?" -ne 0 ]; then
#         echo $test_exec
#         echo ""
#     fi
# done

for script in ./src/test/script/added/context/valid/*.sh; do
    test_exec=$(bash "$script")
    if [ "$?" -ne 0 ]; then
        echo $test_exec
        echo ""
    fi
done

for script in ./src/test/script/added/codegen/*/*.sh; do
    test_exec=$(bash "$script")
    if [ "$?" -ne 0 ]; then
        echo $test_exec
        echo ""
    fi
done


echo "
${bold}These tests SHOULD fail:${normal}"

for script in ./src/test/script/added/context/invalid/*.sh; do  # $(find ./src/test/script/added/ -type f -print)
    test_exec=$(bash "$script")
    if [ "$?" -ne 0 ]; then
        echo $test_exec
        echo ""
    fi
done
