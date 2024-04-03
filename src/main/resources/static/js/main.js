const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Referer': null
    },
    getAllUsers: async () => await fetch('/admin'),
    getAuthUser: async () => await fetch('/user'),
    getUser: async (id) => await fetch(`/user/${id}`),
    saveUser: async (user) => await fetch('/admin/save', {method: 'POST', headers: userFetchService.head, body: JSON.stringify(user)}),
    deleteUser: async (id) => await fetch(`/admin/delete/${id}`, {method: 'DELETE', headers: userFetchService.head})
}

class User {
    constructor(id, firstName, lastName, age, email, userPassword, roleId, roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.userPassword = userPassword;
        this.roles = [{
            roleId: roleId,
            name: roleName
        }]
    }
}

async function getAuth() {
    let response = await userFetchService.getAuthUser();
    let authUser = await response.json();
    console.log(authUser);
    const authEmail = document.querySelector('#authEmail');
    const authRoles = document.querySelector('#authRoles');
    const authInfo = document.querySelector('#authInfo');
    const columns = authInfo.children;
    authEmail.innerText = authUser.email;
    const rolesString = getRoles(authUser.roles);
    authRoles.innerText = rolesString;
    setUserRow(columns, authUser);
    if (rolesString.includes('ADMIN')) {
        getUsers();
    } else {
        document.querySelector('#v-pills-profile').classList.add("show", "active");
        $('#v-pills-tab a[href="#v-pills-profile"]').tab('show');
        document.querySelector("#v-pills-home").remove();
        document.querySelector("#v-pills-home-tab").remove();
    }
    
}

async function handlerUserButton(event) {
    let id = Number(event.target.dataset.index);
    if (id) {
        console.log(id);
        let typeButton = event.target.dataset.type;
        console.log(typeButton);
        let response = await userFetchService.getUser(id);
        let user = await response.json();
        console.log(user);
        if (typeButton == 'edit') {
            inputModal(user, document.querySelector('#editForm'));
            const editBtn = document.querySelector('#editBtn');
            editBtn.addEventListener('click', handlerEditButton);
        } else if (typeButton == 'delete') {
            inputModal(user, document.querySelector('#deleteForm'));
            const deleteBtn = document.querySelector('#deleteBtn');
            deleteBtn.addEventListener('click', handlerDeleteButton);
        }
    }
}

async function handlerEditButton(event) {
    event.preventDefault();
    const elements = event.target.form.elements;
    const data = new User(elements.id.value, elements.firstName.value, elements.lastName.value, elements.age.value,
        elements.email.value, elements.userPassword.value, elements.roleId.value, elements.name.value);
    console.log(data);
    let response = await userFetchService.saveUser(data);
    console.log(response);

    $('#editUser').modal('hide');
    getUsers();
}

async function handlerDeleteButton(event) {
    event.preventDefault();
    console.log(event.target.form.id.value);
    const id = event.target.form.id.value;
    let response = await userFetchService.deleteUser(id);
    console.log(response);
    $('#delUser').modal('hide');
    getUsers();
}

async function handlerAddButton(event) {
    event.preventDefault();
    const elements = event.target.form.elements;
    const data = new User(null, elements.firstName.value, elements.lastName.value, elements.age.value, 
        elements.email.value, elements.userPassword.value, null, elements.name.value);
    console.log(data);
    let response = await userFetchService.saveUser(data);
    console.log(response);

    $('#myTab a[href="#home"]').tab('show');
    getUsers();

}

function setUserRow(columns, thisUser) {
    columns[0].innerText = thisUser.id;
    columns[1].innerText = thisUser.firstName;
    columns[2].innerText = thisUser.lastName;
    columns[3].innerText = thisUser.age;
    columns[4].innerText = thisUser.email;
    columns[5].innerText = getRoles(thisUser.roles);
}

function inputModal(user, element) {
    console.log(user, element);
    element[0].value = user.id;
    element[1].value = user.firstName;
    element[2].value = user.lastName;
    element[3].value = user.age;
    element[4].value = user.email;
    element[5].value = '';
    element[6].value = user.roles[0].roleId;
    element[7].value = user.roles[0].name;
}

function getRoles(listRoles) {
    let roleString = '';
    for (let role of listRoles) {
        roleString += role.name + ' ';
    }
    return roleString;
}

async function getUsers() {
    let response = await userFetchService.getAllUsers();
    let listUsers = await response.json();
    console.log(listUsers);
    const userTable = document.querySelector('#userTable');
    const userRow = document.querySelector('#userRow');
    userTable.innerHTML = '';
    for (let i = 0; i < listUsers.length; i++) {
        let newRow = userRow.content.firstElementChild.cloneNode(true);
        newRow.addEventListener('click', handlerUserButton);
        let columns = newRow.children;
        setUserRow(columns, listUsers[i]);
        columns[6].firstElementChild.dataset.index = listUsers[i].id;
        columns[7].firstElementChild.dataset.index = listUsers[i].id;
        userTable.append(newRow);
    }
    const addBtn = document.querySelector('#addBtn');
    addBtn.addEventListener('click', handlerAddButton);
}

getAuth();