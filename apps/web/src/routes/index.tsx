import { createFileRoute } from "@tanstack/react-router";
import { Button } from "@filey/ui/components/button";

export const Route = createFileRoute("/")({
    component: Index,
});

function Index() {
    return (
        <div>
            <Button>This is a button</Button>
        </div>
    );
}
