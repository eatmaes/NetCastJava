package NetCastJava;

class Response {
    public String response;
    public int status_code;

    public Response(String resp, int code) {
        this.response = resp;
        this.status_code = code;

    }

    public Response() {

    }
}