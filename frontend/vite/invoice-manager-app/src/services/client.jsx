import axios from 'axios';
import {jwtDecode} from "jwt-decode";
import {errorNotification} from "./notification.jsx";
import Cookies from 'js-cookie';

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
    }
})

const url = `${import.meta.env.VITE_API_BASE_URL}`;
export async function login(emailAndPassword){
    try {
        const response = await axios.post(
            `${url}/api/v1/auth/login`,
            emailAndPassword
        );
        localStorage.setItem("token", response.data.token);
        return response.data;
    } catch (e) {
        errorNotification("Oops", e.response);
        throw e;
    }
}

export async function register(usernameEmailAndPassword) {
    try {
        return await axios.post(
            `${url}/api/v1/users/register-user`,
            usernameEmailAndPassword
        );
    } catch (e) {
        throw e;
    }
}

export async function getUser(id){
    try {
        return await axios.get(
            `${url}/api/v1/users/${id}`,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}

export async function updateUser(id, username, password) {
    try {
        return await axios.put(
            `${url}/api/v1/users/update-user/${id}`,
            {username, password},
            getAuthConfig())
    } catch (e) {
        throw e;
    }
}

export async function deleteUser(id){
    try {
        return await axios.delete(
            `${url}/api/v1/users/delete-user/${id}`,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}

export async function confirmAccount(confirmationToken) {
    try {
        return await axios.post(
            `${url}/api/v1/auth/confirm-account?token=${confirmationToken}`
        )
    } catch (e) {
        throw e;
    }
}

export function decodeToken(token) {
    try {
        const decoded = jwtDecode(token);
        if (decoded.exp < Date.now() / 1000) {
            this.logout();
        } else {
            return decoded.sub;
        }
    } catch (err) {
        return false;
    }
}


export async function getCompanies(userId) {
    try {
        return await axios.get(
            `${url}/api/v1/companies/get-companies?userId=${userId}`,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}

export async function createCompany(userId, companyNameAndAccountantEmail) {
    try{
        return await axios.post(
            `${url}/api/v1/companies?userId=${userId}`,
            companyNameAndAccountantEmail,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}
export async function modifyCompany(companyId, companyNameAndAccountantEmail) {
    try {
        return await axios.put(
            `${url}/api/v1/companies/${companyId}`,
            companyNameAndAccountantEmail,
            getAuthConfig()
        );
    } catch (e) {
        throw e;
    }
}

export async function deleteCompany(companyId) {
    try {
        return await axios.delete(
            `${url}/api/v1/companies/${companyId}`,
            getAuthConfig()
        );
    } catch (e) {
        throw e;
    }
}

export async function getInvoices(companyId) {
    try {
        return await axios.get(
            `${url}/api/v1/invoices/get-invoices?companyId=${companyId}`,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}

export async function createInvoice(companyId, invoiceFileAndDescription) {
    try {
        return await axios.post(
            `${url}/api/v1/invoices/upload?companyId=${companyId}`,
            invoiceFileAndDescription,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}

export async function getInvoiceFile(invoiceId) {
    try {
        return await axios.get(
            `${url}/api/v1/invoices/get-invoice-file/${invoiceId}`,
            {
             ...getAuthConfig(),
            responseType: 'blob'
            }
        )
    } catch (e) {
        throw e;
    }
}

export async function deleteInvoices(companyId, invoiceIds){
    try {
        return await axios.delete(
            `${url}/api/v1/invoices/delete-invoices?companyId=${companyId}`,
            {
                ...getAuthConfig(),
                data: invoiceIds
            }
        );
    } catch (e) {
        throw e;
    }
}

export async function sendInvoices(companyId, invoiceIds){
    if (invoiceIds.length === 0) {
        throw new Error('No files selected');
    }
    try {
        return await axios.post(
            `${url}/api/v1/invoices/send-invoices?companyId=${companyId}`,
            invoiceIds,
            getAuthConfig()
        );
    } catch (e) {
        throw e;
    }
}
