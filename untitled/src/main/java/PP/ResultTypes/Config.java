package PP.ResultTypes;

import java.util.List;

public record Config(Integer id, Mode mode, List<String> paths, Action action) {

    public boolean containsNull() {
        return ((id == null) || (mode == null) || (paths == null) || (action == null));
    }

}
