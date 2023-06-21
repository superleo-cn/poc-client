// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract MyEventContract {

    event MyEvent(address indexed sender, string message);

    function triggerEvent(string memory message) public {
        emit MyEvent(msg.sender, message);
    }

}