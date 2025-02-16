import jwt

if __name__ == '__main__':
    encoded = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjZabHF4NXZXTUtZdDgyaENJT05OQyJ9.eyJpc3MiOiJodHRwczovL2Rldi1tc3gzNng0cGFnOHMyMnczLmNhLmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw2NzhmYzY5ZTI1OGEzODZkNmFjMjFjZjIiLCJhdWQiOlsiaW8ub25tZS5ub2JpdGVzIiwiaHR0cHM6Ly9kZXYtbXN4MzZ4NHBhZzhzMjJ3My5jYS5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNzM3ODM4NDQ5LCJleHAiOjE3Mzc5MjQ4NDksInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgc3BvdCByZXBvcnQiLCJhenAiOiJHdVhBVnpNQlA3NTlhTEg1YjBYbFJlekJVOGVCOFhwZCJ9.p2hX4CoRsd8iWXOxYEBKKaQMbF5o86xo04ThU7pnDRYhoqzZGqpKQe-gWi_wgQGpVYN9wiHE0-UwJ6v9bCfoIS2q2hC088fD1g_xO3GBSOFPxUmSI6vnLHZhT5KriX0eK3LO0r-PTnekRk_nbwHYy8HQli4ZDfStEJnZvBd4bJcsO_pKGw61PjXfqtprd0k0Xv_06rn1jcMsl_vITU07OrpkMUFrN0c1zOjHo5fU9tIasmQcAEPDh2RvJ-U9sV079NmfwNZhg_Z186FeO7GlUtyB8tfZKjs0P_P6lIylS0XQSsgPPHdCvi9DMCitiVHxnO5s27q5LFCDwz5hsz4OPA"
    # jwt.decode(encoded, public_key, algorithms=["RS256"])
    # optional_custom_headers = {"User-agent": "custom-user-agent"}

    print(jwt.get_unverified_header(encoded))

    client = jwt.PyJWKClient('https://dev-msx36x4pag8s22w3.ca.auth0.com/.well-known/jwks.json')
    signing_key = client.get_signing_key_from_jwt(encoded).key
    try:
        payload = jwt.decode(
            encoded,
            signing_key,
            options={"verify_exp": False},
            algorithms='RS256',
            audience='io.onme.nobites',
            issuer='https://dev-msx36x4pag8s22w3.ca.auth0.com/',
        )
        print(payload)
    except Exception as error:
        print(error)

    # now, decode_complete to get payload + header
    data = jwt.decode_complete(
        encoded,
        key=signing_key,
        options={"verify_exp": False},
        audience='io.onme.nobites',
        algorithms='RS256',
    )
    payload, header = data["payload"], data["header"]
    print(header)
